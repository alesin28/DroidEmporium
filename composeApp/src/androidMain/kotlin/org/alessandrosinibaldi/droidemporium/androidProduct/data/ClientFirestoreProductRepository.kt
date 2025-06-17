package org.alessandrosinibaldi.droidemporium.androidProduct.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.commonProduct.data.dto.ProductDto
import org.alessandrosinibaldi.droidemporium.commonProduct.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.ProductRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class ClientFirestoreProductRepository : ProductRepository {
    private val firestore = Firebase.firestore
    private val productsCollection = firestore.collection("products")

    override fun searchProducts(query: String): Flow<Result<List<Product>>> = flow {
        try {
            productsCollection.snapshots.collect { querySnapshot ->
                val allProducts = querySnapshot.documents.map { documentSnapshot ->
                    val dto = documentSnapshot.data<ProductDto>()
                    dto.toDomain(id = documentSnapshot.id)
                }
                val filteredList = if (query.isBlank()) {
                    allProducts
                } else {
                    val lowerCaseQuery = query.lowercase()
                    allProducts.filter { product ->
                        product.name.lowercase().contains(lowerCaseQuery)
                    }
                }
                emit(Result.Success(filteredList))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override suspend fun getProductById(id: String): Result<Product?> {
        return try {
            val snapshot = productsCollection.document(id).get()

            if (snapshot.exists) {
                val dto = snapshot.data<ProductDto>()
                val product = dto.toDomain(id = snapshot.id)
                Result.Success(product)
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


}