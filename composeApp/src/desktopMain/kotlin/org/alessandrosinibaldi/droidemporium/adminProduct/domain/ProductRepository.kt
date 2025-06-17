package org.alessandrosinibaldi.droidemporium.adminProduct.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface ProductRepository {
    fun searchProducts(query: String): Flow<Result<List<Product>>>
    suspend fun getProductById(id: String): Result<Product?>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>
    suspend fun addProduct(name: String, description: String, price: Double, stock: Int, isActive: Boolean, categoryId: String): Result<Unit>
}