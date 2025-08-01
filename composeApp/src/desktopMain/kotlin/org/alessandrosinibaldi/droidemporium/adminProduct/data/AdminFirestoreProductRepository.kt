package org.alessandrosinibaldi.droidemporium.adminProduct.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.commonProduct.data.dto.ProductDto
import org.alessandrosinibaldi.droidemporium.commonProduct.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.AdminProductRepository
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import java.io.File


class AdminFirestoreProductRepository(
    private val cloudinaryUploader: CloudinaryUploader
) : AdminProductRepository {
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
                categoryId = product.categoryId,
                imageIds = product.imageIds,
                defaultImageId = product.defaultImageId
            )

            productsCollection.document(product.id).set(productDto)

            println("Product updated: ${product.id}")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("Error updating product ${product.id}: ${e.message}")
            Result.Failure(e)
        }
    }

    override suspend fun changeProductState(productId: String): Result<Unit> {
        return try {
            val document = productsCollection.document(productId)
            val snapshot = document.get()

            if (snapshot.exists) {
                val currentStatus = snapshot.get<Boolean?>("isActive") == true
                val newStatus = !currentStatus

                document.update("isActive" to newStatus)

                println("Changed state for product $productId to isActive=$newStatus")
                Result.Success(Unit)
            } else {
                val error = Exception("Product with ID $productId not found.")
                println(error.message)
                Result.Failure(error)
            }
        } catch (e: Exception) {
            println("Error changing product state for $productId: ${e.message}")
            Result.Failure(e)
        }
    }

    override suspend fun addProduct(
        name: String,
        description: String,
        price: Double,
        stock: Int,
        isActive: Boolean,
        categoryId: String,
        imageIds: List<String>,
        defaultImageId: String
    ): Result<Unit> {
        return try {
            val productDto = ProductDto(
                name = name,
                description = description,
                price = price,
                stock = stock,
                isActive = isActive,
                categoryId = categoryId,
                imageIds = imageIds,
                defaultImageId = defaultImageId
            )
            val ref = productsCollection.add(productDto)
            println("Product added with ID: ${ref.id}")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("Error adding product: ${e.message}")
            Result.Failure(e)
        }
    }

    override suspend fun uploadImageAndGetId(file: File): Result<String> {
        return try {
            val response = cloudinaryUploader.uploadImage(file)
            Result.Success(response.publicId)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
