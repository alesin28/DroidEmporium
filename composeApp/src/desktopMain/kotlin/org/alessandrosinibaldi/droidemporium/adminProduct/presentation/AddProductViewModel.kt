package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository

sealed interface AddProductEvent {
    data object NavigateBack : AddProductEvent
}

class AddProductViewModel(
    private val repository: ProductRepository,
    private val productId: String?
) : ViewModel() {

    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf("")
    var stock by mutableStateOf("")
    var isActive by mutableStateOf(true)

    var isLoading by mutableStateOf(false)
    var isSaving by mutableStateOf(false)

    val isEditMode = productId != null

    init {
        if (isEditMode && productId != null) {
            loadProduct(productId)
        }
    }


    private val _eventChannel = Channel<AddProductEvent>()
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

    fun addProduct() {

        if (isSaving || isLoading) return



        viewModelScope.launch {
            isSaving = true

            if (isEditMode && productId != null) {
                val updatedProduct = Product(
                    id = productId,
                    name = name,
                    description = description,
                    price = price.toDouble(),
                    stock = stock.toInt(),
                    isActive = isActive
                )
                repository.updateProduct(updatedProduct)
            } else {
                repository.addProduct(name, description, price.toDouble(), stock.toInt(), isActive)

            }

            _eventChannel.send(AddProductEvent.NavigateBack)
        }

    }

    private fun loadProduct(id: String) {
        isLoading = true
        viewModelScope.launch {
            val product = repository.getProductById(id)
            if (product != null) {
                name = product.name
                description = product.description
                price = product.price.toString()
                stock = product.stock.toString()
                isActive = product.isActive
            }
            isLoading = false
        }

    }
}