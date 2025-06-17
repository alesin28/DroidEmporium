package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.Category
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun ProductFormScreen(
    navController: NavHostController,
    productId: String?
) {

    val viewModel: ProductFormViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )

    val categories by viewModel.categories.collectAsState()

    val selectedCategoryId = viewModel.categoryId

    LaunchedEffect(true) {
        viewModel.events.collect { event ->
            when (event) {
                is ProductFormEvent.NavigateBack -> {
                    println("NavigateBack event received, popping back stack.")
                    navController.popBackStack()
                }
            }
        }
    }

    ProductFormScreenContent(
        name = viewModel.name,
        description = viewModel.description,
        price = viewModel.price,
        stock = viewModel.stock,
        isActive = viewModel.isActive,
        selectedCategoryId = selectedCategoryId,
        categories = categories,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriceChange = viewModel::onPriceChange,
        onStockChange = viewModel::onStockChange,
        onStatusChange = viewModel::onStatusChange,
        onCategoryChange = viewModel::onCategoryChange,
        onAddProduct = viewModel::addProduct,
        isSaving = viewModel.isSaving,
        isLoading = viewModel.isLoading,
        onNavigateBack = viewModel::onFormCancel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreenContent(
    name: String,
    description: String,
    price: String,
    stock: String,
    isActive: Boolean,
    selectedCategoryId: String?,
    categories: List<Category>,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onStatusChange: (Boolean) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAddProduct: () -> Unit,
    isSaving: Boolean,
    isLoading: Boolean,
    onNavigateBack: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val selectedCategory = remember(selectedCategoryId, categories) {
        categories.find { it.id == selectedCategoryId }
    }


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
            OutlinedTextField(
                value = stock.toString(),
                label = { Text("Stock") },
                onValueChange = onStockChange,
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    if (!isSaving && !isLoading) {
                        expanded = !expanded
                    }
                },
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    enabled = !isSaving && !isLoading,
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = !isSaving && !isLoading
                        )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {

                    categories.forEach { category ->
                        category.id?.let { categoryId ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    onCategoryChange(categoryId)
                                    expanded = false
                                }
                            )
                        }
                    }

                }
            }



            Text("Active")
            Checkbox(
                checked = isActive,
                onCheckedChange = {
                    onStatusChange(!isActive)
                }
            )

            Button(
                onClick = onAddProduct,
                enabled = !isSaving
            ) {
                Text("Add Product")
            }
            Button(onClick = onNavigateBack) {
                Text("Cancel")
            }
        }
    }
}