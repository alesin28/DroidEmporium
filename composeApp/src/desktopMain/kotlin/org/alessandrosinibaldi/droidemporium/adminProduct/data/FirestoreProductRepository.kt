package org.alessandrosinibaldi.droidemporium.adminProduct.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.adminProduct.data.dto.ProductDto
import org.alessandrosinibaldi.droidemporium.adminProduct.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result


class FirestoreProductRepository : ProductRepository {
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

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            val productDto = ProductDto(
                name = product.name,
                description = product.description,
                price = product.price,
                stock = product.stock,
                isActive = product.isActive,
                categoryId = product.categoryId
            )

            productsCollection.document(product.id).set(productDto)

            println("Product updated: ${product.id}")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("Error updating product ${product.id}: ${e.message}")
            Result.Failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            println("Deleting product with ID: $productId")
            productsCollection.document(productId).delete()

            Result.Success(Unit)
        } catch (e: Exception) {
            println("Error deleting product $productId: ${e.message}")
            Result.Failure(e)
        }
    }

    override suspend fun addProduct(
        name: String,
        description: String,
        price: Double,
        stock: Int,
        isActive: Boolean,
        categoryId: String
    ): Result<Unit> {
        return try {
            val productDto = ProductDto(
                name = name,
                description = description,
                price = price,
                stock = stock,
                isActive = isActive,
                categoryId = categoryId
            )
            val ref = productsCollection.add(productDto)
            println("Product added with ID: ${ref.id}")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("Error adding product: ${e.message}")
            Result.Failure(e)
        }
    }
}