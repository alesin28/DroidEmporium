package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

sealed interface ProductFormEvent {
    data object NavigateBack : ProductFormEvent
}

class ProductFormViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val productId: String?
) : ViewModel() {

    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf("")
    var stock by mutableStateOf("")
    var isActive by mutableStateOf(true)
    var categoryId by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
    var isSaving by mutableStateOf(false)

    val isEditMode = productId != null

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        loadAvailableCategories()
        if (isEditMode && productId != null) {
            loadProduct(productId)
        }
    }


    private val _eventChannel = Channel<ProductFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onNameChange(newName: String) {
        name = newName
    }

    fun onDescriptionChange(newDescription: String) {
        description = newDescription
    }

    fun onPriceChange(newPrice: String) {
        price = newPrice
    }

    fun onStockChange(newStock: String) {
        stock = newStock
    }

    fun onStatusChange(newStatus: Boolean) {
        isActive = newStatus
    }

    fun onCategoryChange(selectedCategoryId: String) {
        categoryId = selectedCategoryId
    }

    fun onFormCancel() {
        viewModelScope.launch {
            _eventChannel.send(ProductFormEvent.NavigateBack)
        }
    }

    fun addProduct() {

        if (isSaving || isLoading) return



        viewModelScope.launch {
            val result: Result<Unit> = if (isEditMode && productId != null) {
                val updatedProduct = Product(
                    id = productId,
                    name = name,
                    description = description,
                    price = price.toDoubleOrNull() ?: 0.0,
                    stock = stock.toIntOrNull() ?: 0,
                    isActive = isActive,
                    categoryId = categoryId ?: ""
                )
                productRepository.updateProduct(updatedProduct)
            } else {
                productRepository.addProduct(
                    name = name,
                    description = description,
                    price = price.toDoubleOrNull() ?: 0.0,
                    stock = stock.toIntOrNull() ?: 0,
                    isActive = isActive,
                    categoryId = categoryId ?: ""
                )
            }

            when (result) {
                is Result.Success -> {
                    println("Product saved successfully.")
                    _eventChannel.send(ProductFormEvent.NavigateBack)
                }

                is Result.Failure -> {
                    println("Failed to save product: ${result.exception.message}")
                    isSaving = false
                }
            }
        }

    }

    private fun loadProduct(id: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                when (val result = productRepository.getProductById(id)) {
                    is Result.Success -> {
                        val product = result.data
                        if (product != null) {
                            name = product.name
                            description = product.description
                            price = product.price.toString()
                            stock = product.stock.toString()
                            isActive = product.isActive
                            categoryId = product.categoryId
                        } else {
                            println("Product with ID $id not found.")
                        }
                    }

                    is Result.Failure -> {
                        println("Error loading product: ${result.exception.message}")
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }


    private fun loadAvailableCategories() {
        viewModelScope.launch {
            categoryRepository.searchCategories("")
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val actualList = result.data
                            _categories.value = actualList
                            if (!isEditMode) {
                                categoryId = actualList.first().id
                            }
                        }

                        is Result.Failure -> {
                            println("Error loading available categories: ${result.exception.message}")
                            _categories.value = emptyList()
                        }
                    }
                }
        }

    }


}