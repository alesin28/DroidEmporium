package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository

sealed interface AddProductEvent {
    data object NavigateBack : AddProductEvent
}

class AddProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf("")

    var isSaving by mutableStateOf(false)

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

    fun addProduct() {

        if (isSaving) return
        viewModelScope.launch {
            isSaving = true
            repository.addProduct(name, description, price.toDouble())
            _eventChannel.send(AddProductEvent.NavigateBack)
        }

    }

}