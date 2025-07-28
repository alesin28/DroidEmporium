package org.alessandrosinibaldi.droidemporium.androidProduct.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.ProductRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface ClientProductRepository: ProductRepository {
    fun getNewestProducts(limit: Int): Flow<Result<List<Product>>>
    fun getProductsByCategory(categoryId: String): Flow<Result<List<Product>>>


}