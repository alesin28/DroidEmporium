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
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.OrderRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class FirestoreOrderRepository : OrderRepository {


    private val firestore = Firebase.firestore
    private val ordersCollection = firestore.collection("orders")
    //private val clientsCollection = firestore.collection("clients")


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
                    val lowerCaseQuery = query.lowercase()
                    allOrders.filter { order ->
                        order.clientId.lowercase().contains(lowerCaseQuery)
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

}