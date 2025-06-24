package org.alessandrosinibaldi.droidemporium.adminClient.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.commonClient.data.dto.ClientDto
import org.alessandrosinibaldi.droidemporium.commonClient.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.alessandrosinibaldi.droidemporium.adminClient.domain.AdminClientRepository


class FirestoreClientRepository : AdminClientRepository {

    private val clientsCollection = Firebase.firestore.collection("clients")

    override fun searchClients(query: String): Flow<Result<List<Client>>> = flow {
        try {
            clientsCollection.snapshots.collect { querySnapshot ->

                val allClients = querySnapshot.documents.map { documentSnapshot ->
                    val dto = documentSnapshot.data<ClientDto>()
                    dto.toDomain(id = documentSnapshot.id)
                }

                val filteredList = if (query.isBlank()) {
                    allClients
                } else {
                    val lowerCaseQuery = query.lowercase()
                    allClients.filter { client ->
                        client.displayName.lowercase().contains(lowerCaseQuery) ||
                                client.email.lowercase().contains(lowerCaseQuery)
                    }
                }
                emit(Result.Success(filteredList))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }


    override suspend fun getClientById(id: String): Result<Client?> {
        return try {
            val snapshot = clientsCollection.document(id).get()

            if (snapshot.exists) {
                val client = snapshot.data<ClientDto>().toDomain(id = snapshot.id)
                Result.Success(client)
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override fun getClientsByIds(ids: Set<String>): Flow<Result<List<Client>>> = flow {
        if (ids.isEmpty()) {
            emit(Result.Success(emptyList()))
            return@flow
        }

        try {
            val clients = coroutineScope {
                val deferreds = ids.map { clientId ->
                    async {
                        val snapshot = clientsCollection.document(clientId).get()
                        if (snapshot.exists) {
                            snapshot.data<ClientDto>().toDomain(id = snapshot.id)
                        } else {
                            null
                        }
                    }
                }
                deferreds.awaitAll().filterNotNull()
            }
            emit(Result.Success(clients))

        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}