package org.alessandrosinibaldi.droidemporium.androidCart.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.androidProduct.domain.ClientProductRepository
import org.alessandrosinibaldi.droidemporium.commonCart.data.dto.CartItemDto
import org.alessandrosinibaldi.droidemporium.commonCart.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartItem
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class FirestoreCartRepository(
    private val productRepository: ClientProductRepository
) : CartRepository {
    private val firestore = Firebase.firestore

    private fun getUserCart(clientId: String): CollectionReference {
        return firestore.collection("users").document(clientId).collection("cart")
    }

    override fun getCart(clientId: String): Flow<Result<List<CartItem>>> = flow {
        try {
            getUserCart(clientId).snapshots.collect { querySnapshot ->
                val cartItems = querySnapshot.documents.map { document ->
                    document.data<CartItemDto>().toDomain(productId = document.id)
                }
                emit(Result.Success(cartItems))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override suspend fun addToCart(
        clientId: String,
        productId: String,
        quantity: Int
    ): Result<Unit> {
        return try {
            val productResult = productRepository.getProductById(productId)
            val product = when (productResult) {
                is Result.Success -> productResult.data
                is Result.Failure -> throw productResult.exception // Propagate error
            }

            if (product == null) {
                return Result.Failure(Exception("Product with ID $productId not found."))
            }

            firestore.runTransaction {
                val cartItemRef = getUserCart(clientId).document(productId)
                val snapshot = get(cartItemRef)

                if (snapshot.exists) {
                    val existingQuantity = snapshot.data<CartItemDto>().quantity
                    update(cartItemRef, "quantity" to (existingQuantity + quantity))
                } else {
                    val newCartItemDto = CartItemDto(
                        quantity = quantity,
                        productName = product.name,
                        productPrice = product.price,
                        productImageUrl = "https://res.cloudinary.com/dovupsygm/image/upload/w_150,h_150,c_fill/${product.defaultImageId}",
                        addedAt = Timestamp.now()
                    )
                    set(cartItemRef, newCartItemDto)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun removeFromCart(
        clientId: String,
        productId: String
    ): Result<Unit> {
        return try {
            getUserCart(clientId).document(productId).delete()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun updateQuantity(
        clientId: String,
        productId: String,
        newQuantity: Int
    ): Result<Unit> {
        if (newQuantity <= 0) {
            return removeFromCart(clientId, productId)
        }
        return try {
            val cartItemRef = getUserCart(clientId).document(productId)
            cartItemRef.update("quantity" to newQuantity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


}