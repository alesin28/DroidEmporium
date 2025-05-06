package org.alessandrosinibaldi.droidemporium.adminClient.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.adminClient.domain.Client
import org.alessandrosinibaldi.droidemporium.adminClient.domain.ClientRepository

class FirestoreClientRepository : ClientRepository {

    private val firestore = Firebase.firestore

    override fun searchClients(query: String?) = flow {
        firestore.collection("clients").snapshots.collect { querySnapshot ->
            val clients = querySnapshot.documents.map { documentSnapshot ->

                Client(
                    id = documentSnapshot.id,
                    displayName = documentSnapshot.data<Client>().displayName,
                    email = documentSnapshot.data<Client>().email,
                    phoneNumber = documentSnapshot.data<Client>().phoneNumber,
                    isActive = documentSnapshot.data<Client>().isActive
                )


            }
            val filteredClients = if (!query.isNullOrBlank()) {
                val lowerCaseQuery = query.lowercase()
                clients.filter { client ->
                    client.displayName.lowercase().contains(lowerCaseQuery.toString()) ||
                            client.email.lowercase().contains(lowerCaseQuery.toString())
                }
            } else {
                clients
            }
            emit(filteredClients)
        }

    }
}