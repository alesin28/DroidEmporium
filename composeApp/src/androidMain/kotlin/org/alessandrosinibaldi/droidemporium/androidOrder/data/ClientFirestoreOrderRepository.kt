package org.alessandrosinibaldi.droidemporium.androidOrder.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.androidOrder.domain.ClientOrderRepository
import org.alessandrosinibaldi.droidemporium.commonAddress.data.dto.AddressDto
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartItem
import org.alessandrosinibaldi.droidemporium.commonOrder.data.dto.OrderDto
import org.alessandrosinibaldi.droidemporium.commonOrder.data.dto.OrderLineDto
import org.alessandrosinibaldi.droidemporium.commonOrder.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class ClientFirestoreOrderRepository : ClientOrderRepository {

    private val firestore = Firebase.firestore
    private val ordersCollection = firestore.collection("orders")

    override suspend fun placeOrder(
        clientId: String,
        clientName: String,
        cartItems: List<CartItem>,
        address: Address,
        totalAmount: Double
    ): Result<Unit> {
        return try {
            val addressDto = AddressDto(
                label = address.label,
                name = address.name,
                surname = address.surname,
                street = address.street,
                city = address.city,
                province = address.province,
                postalCode = address.postalCode,
                country = address.country,
                isDefault = address.isDefault
            )

            val orderDto = OrderDto(
                clientId = clientId,
                clientName = clientName,
                orderDate = Timestamp.now(),
                totalAmount = totalAmount,
                address = addressDto
            )

            val newOrderRef = ordersCollection.add(orderDto)

            cartItems.forEach { item ->
                val lineDto = OrderLineDto(
                    productId = item.productId,
                    productName = item.productName,
                    quantity = item.quantity,
                    priceAtPurchase = item.productPrice
                )
                newOrderRef.collection("orderLines").add(lineDto)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(e)
        }
    }

    override suspend fun getOrderById(id: String): Result<Order?> {
        return try {
            val orderSnapshot = ordersCollection.document(id).get()

            if (orderSnapshot.exists) {
                val linesSnapshot = orderSnapshot.reference.collection("orderLines").get()
                val domainLines = linesSnapshot.documents.map { lineDoc ->
                    lineDoc.data<OrderLineDto>().toDomain(id = lineDoc.id)
                }

                val orderDto = orderSnapshot.data<OrderDto>()
                val finalOrder = orderDto.toDomain(id = orderSnapshot.id, lines = domainLines)

                Result.Success(finalOrder)
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override fun searchOrders(query: String): Flow<Result<List<Order>>> = flow {
        try {
            ordersCollection.snapshots.collect { querySnapshot ->
                val allOrders = coroutineScope {
                    querySnapshot.documents.map { orderDoc ->
                        async {
                            val linesSnapshot = orderDoc.reference.collection("orderLines").get()
                            val domainLines = linesSnapshot.documents.map { lineDoc ->
                                lineDoc.data<OrderLineDto>().toDomain(id = lineDoc.id)
                            }
                            val orderDto = orderDoc.data<OrderDto>()
                            orderDto.toDomain(id = orderDoc.id, lines = domainLines)
                        }
                    }
                }.awaitAll()

                val filteredList = if (query.isBlank()) {
                    allOrders
                } else {
                    val lowerCaseQuery = query.lowercase().trim()
                    allOrders.filter { order ->
                        order.id.lowercase().contains(lowerCaseQuery) ||
                                order.clientName.lowercase().contains(lowerCaseQuery) ||
                                order.lines.any {
                                    it.productName.lowercase().contains(lowerCaseQuery)
                                }
                    }
                }

                emit(Result.Success(filteredList))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}