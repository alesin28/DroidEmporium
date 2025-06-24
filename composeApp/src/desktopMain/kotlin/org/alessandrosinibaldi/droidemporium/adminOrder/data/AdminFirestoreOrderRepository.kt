package org.alessandrosinibaldi.droidemporium.adminOrder.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.adminOrder.data.dto.OrderDto
import org.alessandrosinibaldi.droidemporium.adminOrder.data.dto.OrderLineDto
import org.alessandrosinibaldi.droidemporium.adminOrder.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.AdminOrderRepository
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class AdminFirestoreOrderRepository : AdminOrderRepository {

    private val firestore = Firebase.firestore
    private val ordersCollection = Firebase.firestore.collection("orders")


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
                                order.lines.any { orderLine ->
                                    orderLine.productName.lowercase().contains(lowerCaseQuery)
                                }
                    }
                }
                emit(Result.Success(filteredList))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
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

    override suspend fun getOrdersByProduct(productId: String): Result<List<Order>> {
        return try {
            val matchingLinesSnapshot = firestore.collectionGroup("orderLines")
                .where { "productId" equalTo productId }
                .get()

            val orderIds = matchingLinesSnapshot.documents.mapNotNull { doc ->
                doc.reference.parent.parent?.id
            }.toSet()

            if (orderIds.isEmpty()) {
                return Result.Success(emptyList())
            }

            val orders = coroutineScope {
                orderIds.map { id ->
                    async { getOrderById(id) }
                }.awaitAll()
            }
                .mapNotNull { result ->
                    when (result) {
                        is Result.Success -> result.data
                        is Result.Failure -> {
                            println("Failed to fetch an order details: ${result.exception.message}")
                            null
                        }
                    }
                }

            Result.Success(orders)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getOrdersByClient(clientId: String): Result<List<Order>> {
        return try {
            val querySnapshot = ordersCollection
                .where { "clientId" equalTo clientId }
                .get()

            val orders = coroutineScope {
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

            Result.Success(orders)
        } catch (e: Exception) {
            Result.Failure(e)
        }

    }
}