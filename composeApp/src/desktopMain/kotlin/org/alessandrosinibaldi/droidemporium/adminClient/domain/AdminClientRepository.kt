package org.alessandrosinibaldi.droidemporium.adminClient.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.commonClient.domain.ClientRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface AdminClientRepository : ClientRepository {

    fun searchClients(query: String): Flow<Result<List<Client>>>


    fun getClientsByIds(ids: Set<String>): Flow<Result<List<Client>>>

}