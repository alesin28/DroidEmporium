package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import org.alessandrosinibaldi.droidemporium.adminProduct.data.CloudinaryUploader
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.AdminProductRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import java.io.File

sealed interface ProductFormEvent {
    data object NavigateBack : ProductFormEvent
}

class ProductFormViewModel(
    private val adminProductRepository: AdminProductRepository,
    private val categoryRepository: CategoryRepository,
    private val cloudinaryUploader: CloudinaryUploader,
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
    private val _eventChannel = Channel<ProductFormEvent>()
    val events = _eventChannel.receiveAsFlow()
    val cloudinaryCloudName = cloudinaryUploader.cloudName

    val selectedLocalFiles = mutableStateListOf<File>()
    val existingImageIds = mutableStateListOf<String>()
    var defaultImageId by mutableStateOf("")

    init {
        loadAvailableCategories()
        if (isEditMode && productId != null) {
            loadProduct(productId)
        }
    }

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
        viewModelScope.launch { _eventChannel.send(ProductFormEvent.NavigateBack) }
    }

    fun onLocalFilesSelected(files: List<File>) {
        selectedLocalFiles.clear()
        selectedLocalFiles.addAll(files)
    }

    fun onDefaultImageIdChange(newId: String) {
        defaultImageId = newId
    }

    fun saveProduct() {
        if (isSaving || isLoading) return
        isSaving = true

        viewModelScope.launch {
            try {
                val newlyUploadedIds = selectedLocalFiles.map { file ->
                    cloudinaryUploader.uploadImage(file)
                }

                val allImageIds = existingImageIds + newlyUploadedIds

                val finalDefaultImageId = when {

                    newlyUploadedIds.isNotEmpty() -> newlyUploadedIds.first()
                    defaultImageId.isNotBlank() -> defaultImageId
                    else -> ""
                }

                val result: Result<Unit> = if (isEditMode && productId != null) {
                    val updatedProduct = Product(
                        id = productId, name = name, description = description,
                        price = price.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        isActive = isActive, categoryId = categoryId ?: "",
                        imageIds = allImageIds,
                        defaultImageId = finalDefaultImageId
                    )
                    adminProductRepository.updateProduct(updatedProduct)
                } else {
                    adminProductRepository.addProduct(
                        name = name, description = description,
                        price = price.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        isActive = isActive, categoryId = categoryId ?: "",
                        imageIds = allImageIds,
                        defaultImageId = finalDefaultImageId
                    )
                }

                when (result) {
                    is Result.Success -> {
                        println("Product saved successfully.")
                        _eventChannel.send(ProductFormEvent.NavigateBack)
                    }

                    is Result.Failure -> {
                        println("Failed to save product: ${result.exception.message}")
                    }
                }
            } catch (e: Exception) {
                println("An error occurred during image upload or saving: ${e.message}")
            } finally {
                isSaving = false
            }
        }
    }

    private fun loadProduct(id: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                when (val result = adminProductRepository.getProductById(id)) {
                    is Result.Success -> {
                        result.data?.let { product ->
                            name = product.name
                            description = product.description
                            price = product.price.toString()
                            stock = product.stock.toString()
                            isActive = product.isActive
                            categoryId = product.categoryId
                            existingImageIds.clear()
                            existingImageIds.addAll(product.imageIds)
                            defaultImageId = product.defaultImageId
                        } ?: println("Product with ID $id not found.")
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
            categoryRepository.searchCategories("").collect { result ->
                when (result) {
                    is Result.Success -> {
                        val actualList = result.data
                        _categories.value = actualList
                        if (!isEditMode && categoryId == null) {
                            categoryId = actualList.firstOrNull()?.id
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