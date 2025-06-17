package org.alessandrosinibaldi.droidemporium.commonProduct.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface ProductRepository {
    fun searchProducts(query: String): Flow<Result<List<Product>>>
    suspend fun getProductById(id: String): Result<Product?>
}