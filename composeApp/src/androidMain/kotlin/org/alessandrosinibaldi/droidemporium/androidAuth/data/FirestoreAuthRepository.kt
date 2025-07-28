package org.alessandrosinibaldi.droidemporium.androidAuth.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.commonClient.data.dto.ClientDto
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import kotlinx.coroutines.flow.map
import org.alessandrosinibaldi.droidemporium.commonClient.data.dto.toDomain


class FirestoreAuthRepository: AuthRepository {

    private val auth = Firebase.auth
    private val clientsCollection = Firebase.firestore.collection("clients")

    override suspend fun signup(
        displayName: String,
        email: String,
        phoneNumber: String?,
        password: String
    ): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password)
            val user = authResult.user ?: throw IllegalStateException("Firebase user not created.")
            val uid = user.uid

            val clientDto = ClientDto(
                displayName = displayName,
                email = email,
                phoneNumber = phoneNumber
            )
            clientsCollection.document(uid).set(clientDto)

            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(e)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(e)
        }    }

    override suspend fun logout() {
        auth.signOut()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCurrentUser(): Flow<Client?> {
        return auth.authStateChanged.flatMapLatest { firebaseUser ->
            if (firebaseUser == null) {
                flowOf(null)
            } else {
                clientsCollection.document(firebaseUser.uid).snapshots.map { snapshot ->
                    if (snapshot.exists) {
                        snapshot.data<ClientDto>().toDomain(id = snapshot.id)
                    } else {
                        null
                    }
                }
            }
        }
    }


}