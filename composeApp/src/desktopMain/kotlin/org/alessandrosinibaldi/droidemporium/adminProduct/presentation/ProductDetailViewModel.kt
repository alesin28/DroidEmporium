package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.AdminOrderRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.AdminProductRepository
import org.alessandrosinibaldi.droidemporium.adminReview.domain.AdminReviewRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class ProductDetailViewModel(
    private val adminProductRepository: AdminProductRepository,
    private val categoryRepository: CategoryRepository,
    private val adminReviewRepository: AdminReviewRepository,
    private val adminOrderRepository: AdminOrderRepository,
    private val productId: String?
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    private val _category = MutableStateFlow<Category?>(null)
    val category: StateFlow<Category?> = _category.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        if (productId != null) {
            loadProduct(productId)
        } else {
            _isLoading.value = false

        }
    }

    private fun loadProduct(id: String) {
        viewModelScope.launch {
            _product.value = when (val productResult = adminProductRepository.getProductById(id)) {
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
                _reviews.value =
                    when (val reviewsResult = adminReviewRepository.getReviewsForProduct(id)) {
                        is Result.Success -> {
                            reviewsResult.data
                        }

                        is Result.Failure -> {
                            println("Error fetching reviews for product detail: ${reviewsResult.exception.message}")
                            null
                        }
                    }!!
                _orders.value =
                    when (val ordersResult = adminOrderRepository.getOrdersByProduct(id)) {
                        is Result.Success -> {
                            ordersResult.data
                        }

                        is Result.Failure -> {
                            println("Error fetching orders for product detail: ${ordersResult.exception.message}")
                            null
                        }
                    }!!
                _isLoading.value = false
            }
        }
    }
}




