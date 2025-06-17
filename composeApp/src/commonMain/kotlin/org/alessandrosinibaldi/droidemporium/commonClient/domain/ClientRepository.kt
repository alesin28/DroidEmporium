package org.alessandrosinibaldi.droidemporium.commonClient.domain

import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface ClientRepository {

    suspend fun getClientById(id: String): Result<Client?>


}