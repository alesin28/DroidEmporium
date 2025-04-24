package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel = koinViewModel(),
    navController: NavHostController
) {

    LaunchedEffect(true) {
        viewModel.events.collect { event ->
            when (event) {
                is AddProductEvent.NavigateBack -> {
                    println("NavigateBack event received, popping back stack.")
                    navController.popBackStack()
                }
            }
        }
    }

    AddProductScreenContent(
        name = viewModel.name,
        description = viewModel.description,
        price = viewModel.price,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriceChange = viewModel::onPriceChange,
        onAddProduct = viewModel::addProduct,
        isSaving = viewModel.isSaving
    )
}

@Composable
fun AddProductScreenContent(
    name: String,
    description: String,
    price: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onAddProduct: () -> Unit,
    isSaving: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFADD8E6)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                label = { Text("Name") },
                onValueChange = onNameChange,
            )
            OutlinedTextField(
                value = description,
                label = { Text("Description") },
                onValueChange = onDescriptionChange,

                )
            OutlinedTextField(
                value = price,
                label = { Text("Price") },
                onValueChange = onPriceChange,
            )

            Button(
                onClick = onAddProduct,
                enabled = !isSaving
            ) {
                Text("Add Product")
            }
        }
    }
}