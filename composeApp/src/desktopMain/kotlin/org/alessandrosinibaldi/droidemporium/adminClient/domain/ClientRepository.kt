package org.alessandrosinibaldi.droidemporium.adminClient.domain

import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    fun searchClients(query: String? = null): Flow<List<Client>>

}