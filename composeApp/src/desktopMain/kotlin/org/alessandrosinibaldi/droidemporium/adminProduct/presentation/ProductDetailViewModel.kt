package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val productId: String?
) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    private val _category = MutableStateFlow<Category?>(null)
    val category: StateFlow<Category?> = _category.asStateFlow()

    init {
        if (productId != null) {
            loadProduct(productId)
        }
    }

    private fun loadProduct(id: String) {
        viewModelScope.launch {
            _product.value = when (val productResult = productRepository.getProductById(id)) {
                is Result.Success -> {
                    productResult.data
                }

                is Result.Failure -> {
                    println("Error fetching product detail: ${productResult.exception.message}")
                    null
                }
            }

            val fetchedProductId = _product.value?.categoryId
            if (fetchedProductId != null) {
                _category.value = when (val categoryResult =
                    categoryRepository.getCategoryById(fetchedProductId)) {
                    is Result.Success -> {
                        categoryResult.data
                    }

                    is Result.Failure -> {
                        println("Error fetching category for product detail: ${categoryResult.exception.message}")
                        null
                    }
                }
            }
        }
    }

}

