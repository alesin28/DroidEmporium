package org.alessandrosinibaldi.droidemporium.adminOrder.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.adminClient.domain.Client
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.OrderLine
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.OrderRepository

class FirestoreOrderRepository : OrderRepository {


    private val firestore = Firebase.firestore
    private val ordersCollection = firestore.collection("orders")
    private val clientsCollection = firestore.collection("clients")


    override fun searchOrders(query: String?): Flow<Pair<List<Order>, List<Client>>> = flow {
        ordersCollection.snapshots.collect { querySnapshot ->
            val orders = coroutineScope {
                querySnapshot.documents.map { documentSnapshot ->
                    async {
                        val baseOrderData = documentSnapshot.data<Order>()

                        val fetchedOrderLines = getOrderLines(documentSnapshot.id)

                        Order(
                            id = documentSnapshot.id,
                            clientId = baseOrderData.clientId,
                            orderDate = baseOrderData.orderDate,
                            totalAmount = baseOrderData.totalAmount,
                            lines = fetchedOrderLines
                        )
                    }
                }.awaitAll()
            }
            val clientIds = orders.map { it.clientId }
                .distinct()


            val clients = mutableListOf<Client>()
            if (clientIds.isNotEmpty()) {
                coroutineScope {
                    val coroutineClients = clientIds.map { clientId ->
                        async {
                            val clientDocSnapshot =
                                clientsCollection.document(clientId).get() // Suspend call
                            clientDocSnapshot.data<Client>().copy(id = clientDocSnapshot.id)


                        }
                    }
                    clients.addAll(coroutineClients.awaitAll())
                }
            }

            emit(Pair(orders, clients))
        }


    }

    override suspend fun getOrderById(id: String): Pair<Order, Client>? {

        val orderDocSnapshot = ordersCollection.document(id).get()
        if (orderDocSnapshot.exists) {
            val orderData = orderDocSnapshot.data<Order>()

            val lines = getOrderLines(orderDocSnapshot.id)

            val order = Order(
                id = orderDocSnapshot.id,
                clientId = orderData.clientId,
                orderDate = orderData.orderDate,
                totalAmount = orderData.totalAmount,
                lines = lines
            )


            val clientDocSnapshot = clientsCollection.document(order.clientId).get()

            if (clientDocSnapshot.exists) {
                val client = clientDocSnapshot.data<Client>()
                    .copy(id = clientDocSnapshot.id)
                return Pair(order, client)
            }
        }
        return null
    }


    private suspend fun getOrderLines(orderId: String): List<OrderLine> {
        val linesSnapshot = ordersCollection.document(orderId)
            .collection("orderLines")
            .get()

        return linesSnapshot.documents.map { lineDoc ->
            val orderLineData = lineDoc.data<OrderLine>()
            orderLineData.copy(id = lineDoc.id)
        }

    }
}