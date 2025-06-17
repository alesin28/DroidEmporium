package org.alessandrosinibaldi.droidemporium.adminClient.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface ClientRepository {

    fun searchClients(query: String): Flow<Result<List<Client>>>

    suspend fun getClientById(id: String): Result<Client?>

    fun getClientsByIds(ids: Set<String>): Flow<Result<List<Client>>>

}