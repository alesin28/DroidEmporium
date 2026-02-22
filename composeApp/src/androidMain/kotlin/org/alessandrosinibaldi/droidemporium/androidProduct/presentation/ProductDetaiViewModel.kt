package org.alessandrosinibaldi.droidemporium.androidProduct.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.androidProduct.domain.ClientProductRepository
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.commonReview.domain.ReviewRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import kotlinx.coroutines.flow.firstOrNull

class ProductDetailViewModel(
    private val productId: String,
    private val clientProductRepository: ClientProductRepository,
    private val categoryRepository: CategoryRepository,
    private val clientReviewRepository: ReviewRepository,
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _isAddingToCart = MutableStateFlow(false)
    val isAddingToCart = _isAddingToCart.asStateFlow()

    private val _cartMessage = MutableStateFlow<String?>(null)
    val cartMessage = _cartMessage.asStateFlow()

    private val _product = MutableStateFlow<Product?>(null)
    val product = _product.asStateFlow()

    private val _category = MutableStateFlow<Category?>(null)
    val category = _category.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadProductDetails()
    }

    private fun loadProductDetails() {
        viewModelScope.launch {
            _isLoading.value = true

            when (val productResult = clientProductRepository.getProductById(productId)) {
                is Result.Success -> {
                    _product.value = productResult.data
                    if (productResult.data == null) {
                        _error.value = "Product not found."
                        _isLoading.value = false
                        return@launch
                    }
                }
                is Result.Failure -> {
                    _error.value = "Error fetching product: ${productResult.exception.message}"
                    _isLoading.value = false
                    return@launch
                }
            }

            val product = _product.value!!

            coroutineScope {
                val categoryAsync = async { categoryRepository.getCategoryById(product.categoryId) }
                val reviewsAsync = async { clientReviewRepository.getReviewsForProduct(product.id) }

                when (val categoryResult = categoryAsync.await()) {
                    is Result.Success -> _category.value = categoryResult.data
                    is Result.Failure -> Log.e("ProductDetail", "Failed to load category: ${categoryResult.exception.message}")
                }

                when (val reviewsResult = reviewsAsync.await()) {
                    is Result.Success -> _reviews.value = reviewsResult.data
                    is Result.Failure -> Log.e("ProductDetail", "Failed to load reviews: ${reviewsResult.exception.message}")
                }
            }

            _isLoading.value = false
        }
    }

    fun addToCart() {
        val currentProduct = _product.value ?: return

        viewModelScope.launch {
            _isAddingToCart.value = true

            val user = authRepository.getCurrentUser().firstOrNull()

            if (user == null) {
                _cartMessage.value = "You must be logged in to add items to the cart."
                _isAddingToCart.value = false
                return@launch
            }

            val result = cartRepository.addToCart(
                clientId = user.id,
                productId = currentProduct.id,
                quantity = 1
            )

            when (result) {
                is Result.Success -> _cartMessage.value = "Added to cart."
                is Result.Failure -> _cartMessage.value = "Failed to add to cart."
            }

            _isAddingToCart.value = false
        }
    }

    fun onCartMessageShown() {
        _cartMessage.value = null
    }

}