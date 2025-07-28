package org.alessandrosinibaldi.droidemporium.androidProduct.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.androidProduct.domain.ClientProductRepository
import org.alessandrosinibaldi.droidemporium.commonProduct.data.dto.ProductDto
import org.alessandrosinibaldi.droidemporium.commonProduct.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import kotlin.coroutines.cancellation.CancellationException


class ClientFirestoreProductRepository : ClientProductRepository {
    private val firestore = Firebase.firestore
    private val productsCollection = firestore.collection("products")

    override fun searchProducts(query: String): Flow<Result<List<Product>>> = flow {
        try {
            val querySnapshot = productsCollection.where { "isActive" equalTo true }.get()
            val allActiveProducts = querySnapshot.documents.map { documentSnapshot ->
                val dto = documentSnapshot.data<ProductDto>()
                dto.toDomain(id = documentSnapshot.id)
            }
            val filteredList = if (query.isBlank()) {
                allActiveProducts
            } else {
                val lowerCaseQuery = query.lowercase()
                allActiveProducts.filter { product ->
                    product.name.lowercase().contains(lowerCaseQuery)
                }
            }
            emit(Result.Success(filteredList))
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            emit(Result.Failure(e))
        }
    }


    override suspend fun getProductById(id: String): Result<Product?> {
        return try {
            val snapshot = productsCollection.document(id).get()
            if (snapshot.exists) {
                val dto = snapshot.data<ProductDto>()
                if (dto.isActive) {
                    val product = dto.toDomain(id = snapshot.id)
                    Result.Success(product)
                } else {
                    Result.Success(null)
                }
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


    override fun getNewestProducts(limit: Int): Flow<Result<List<Product>>> = flow {
        try {
            val querySnapshot = productsCollection
                .where { "isActive" equalTo true }
                .orderBy("createdAt", Direction.DESCENDING)
                .limit(limit.toLong())
                .get()

            val products = querySnapshot.documents.map { documentSnapshot ->
                val dto = documentSnapshot.data<ProductDto>()
                dto.toDomain(id = documentSnapshot.id)
            }
            emit(Result.Success(products))
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            emit(Result.Failure(e))
        }
    }

    override fun getProductsByCategory(categoryId: String): Flow<Result<List<Product>>> = flow {
        try {
            val querySnapshot = productsCollection
                .where { "isActive" equalTo true }
                .where { "categoryId" equalTo categoryId }
                .get()

            val products = querySnapshot.documents.map { doc ->
                doc.data<ProductDto>().toDomain(doc.id)
            }
            emit(Result.Success(products))
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            emit(Result.Failure(e))
        }
    }

}