package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.seiko.imageloader.rememberImagePainter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import javax.swing.JFileChooser

@Composable
fun ProductFormScreen(
    navController: NavHostController,
    productId: String?
) {
    val viewModel: ProductFormViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )

    val categories by viewModel.categories.collectAsState()

    LaunchedEffect(true) {
        viewModel.events.collect { event ->
            when (event) {
                is ProductFormEvent.NavigateBack -> {
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
        selectedCategoryId = viewModel.categoryId,
        categories = categories,
        isSaving = viewModel.isSaving,
        isLoading = viewModel.isLoading,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriceChange = viewModel::onPriceChange,
        onStockChange = viewModel::onStockChange,
        onStatusChange = viewModel::onStatusChange,
        onCategoryChange = viewModel::onCategoryChange,
        onSaveProduct = viewModel::saveProduct,
        onNavigateBack = viewModel::onFormCancel,
        existingImageIds = viewModel.existingImageIds,
        selectedLocalFiles = viewModel.selectedLocalFiles,
        defaultImageId = viewModel.defaultImageId,
        onLocalFilesSelected = viewModel::onLocalFilesSelected,
        onDefaultImageIdChange = viewModel::onDefaultImageIdChange,
        cloudinaryCloudName = viewModel.cloudinaryCloudName,
        isEditMode = viewModel.isEditMode
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
    isSaving: Boolean,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onStatusChange: (Boolean) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSaveProduct: () -> Unit,
    onNavigateBack: () -> Unit,
    existingImageIds: List<String>,
    selectedLocalFiles: List<File>,
    defaultImageId: String,
    onLocalFilesSelected: (List<File>) -> Unit,
    onDefaultImageIdChange: (String) -> Unit,
    cloudinaryCloudName: String,
    isEditMode: Boolean
) {

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var imageDropdownExpanded by remember { mutableStateOf(false) }

    val selectedCategory = remember(selectedCategoryId, categories) {
        categories.find { it.id == selectedCategoryId }
    }

    val fileChooser = JFileChooser().apply {
        isMultiSelectionEnabled = true
        fileFilter =
            javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "gif", "jpeg")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    label = { Text("Name") },
                    onValueChange = onNameChange
                )
            }
            item {
                OutlinedTextField(
                    value = description,
                    label = { Text("Description") },
                    onValueChange = onDescriptionChange
                )
            }
            item {
                OutlinedTextField(
                    value = price,
                    label = { Text("Price") },
                    onValueChange = onPriceChange
                )
            }
            item {
                OutlinedTextField(
                    value = stock,
                    label = { Text("Stock") },
                    onValueChange = onStockChange
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it } // Updated here
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                        value = selectedCategory?.name ?: "Select Category",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { category ->
                            category.id?.let { categoryId ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        onCategoryChange(categoryId)
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Active")
                    Checkbox(checked = isActive, onCheckedChange = onStatusChange)
                }
            }

            item {
                Button(onClick = {
                    val result = fileChooser.showOpenDialog(null)
                    if (result == JFileChooser.APPROVE_OPTION) {
                        onLocalFilesSelected(fileChooser.selectedFiles.toList())
                    }
                }) {
                    Text("Select Images to Upload")
                }
            }

            if (selectedLocalFiles.isNotEmpty()) {
                item { Text("New files to upload:", style = MaterialTheme.typography.titleMedium) }
                items(selectedLocalFiles.size) { index ->
                    Text("- ${selectedLocalFiles[index].name}")
                }
            }

            if (isEditMode && existingImageIds.isNotEmpty()) {
                item { Text("Current Images:", style = MaterialTheme.typography.titleMedium) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(existingImageIds.size) { index ->
                            val imageId = existingImageIds[index]
                            val imageUrl =
                                "https://res.cloudinary.com/$cloudinaryCloudName/image/upload/w_100,h_100,c_fill/$imageId"
                            Image(
                                painter = rememberImagePainter(imageUrl),
                                contentDescription = imageId,
                                modifier = Modifier.size(100.dp).background(Color.Gray)
                            )
                        }
                    }
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = imageDropdownExpanded,
                        onExpandedChange = { imageDropdownExpanded = it } // Updated here too
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            ),
                            value = defaultImageId,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Default Image") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = imageDropdownExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = imageDropdownExpanded,
                            onDismissRequest = { imageDropdownExpanded = false }
                        ) {
                            existingImageIds.forEach { imageId ->
                                DropdownMenuItem(text = { Text(imageId) }, onClick = {
                                    onDefaultImageIdChange(imageId)
                                    imageDropdownExpanded = false
                                })
                            }
                        }
                    }
                }
            }

            item {
                Button(onClick = onSaveProduct, enabled = !isSaving && !isLoading) {
                    Text(if (isEditMode) "Update Product" else "Add Product")
                }
            }
            item {
                Button(onClick = onNavigateBack) {
                    Text("Cancel")
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}

