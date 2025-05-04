package org.alessandrosinibaldi.droidemporium.adminProduct.domain

import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun searchProducts(query: String? = null): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    suspend fun addProduct(name: String, description: String?, price: Double, stock: Int, isActive: Boolean, categoryId: String)
}