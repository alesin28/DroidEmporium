package org.alessandrosinibaldi.droidemporium.adminProduct.domain

import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.ProductRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import java.io.File

interface AdminProductRepository : ProductRepository {
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>
    suspend fun addProduct(
        name: String,
        description: String,
        price: Double,
        stock: Int,
        isActive: Boolean,
        categoryId: String,
        imageIds: List<String>,
        defaultImageId: String
    ): Result<Unit>
    suspend fun uploadImageAndGetId(file: File): Result<String>

}