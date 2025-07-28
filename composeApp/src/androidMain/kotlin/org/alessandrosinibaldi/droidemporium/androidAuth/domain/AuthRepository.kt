package org.alessandrosinibaldi.droidemporium.androidAuth.domain


import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface AuthRepository {

    suspend fun signup(displayName: String, email: String, phoneNumber: String?, password: String): Result<Unit>

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun logout()

    fun getCurrentUser(): Flow<Client?>

}