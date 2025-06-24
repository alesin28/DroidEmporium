package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.seiko.imageloader.rememberImagePainter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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

    val selectedCategory = remember(selectedCategoryId, categories) {
        categories.find { it.id == selectedCategoryId }
    }

    val fileChooser = remember {
        JFileChooser().apply {
            isMultiSelectionEnabled = true
            fileFilter = FileNameExtensionFilter("Images", "jpg", "png", "gif", "jpeg")
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 800.dp),
            contentPadding = PaddingValues(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = if (isEditMode) "Edit Product" else "Add New Product",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            item {
                FormSection(title = "Core Details") {
                    OutlinedTextField(
                        value = name,
                        label = { Text("Product Name") },
                        onValueChange = onNameChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        label = { Text("Description") },
                        onValueChange = onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = price,
                            label = { Text("Price") },
                            onValueChange = onPriceChange,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = stock,
                            label = { Text("Stock") },
                            onValueChange = onStockChange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
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
                                category.id.let { categoryId ->
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(checked = isActive, onCheckedChange = onStatusChange)
                        Spacer(Modifier.width(8.dp))
                        Text("Product is Active", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                FormSection(title = "Manage Images") {
                    if (isEditMode && existingImageIds.isNotEmpty()) {
                        Text(
                            "Current Images",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            existingImageIds.forEach { imageId ->
                                ImageCard(
                                    imageId = imageId,
                                    cloudinaryCloudName = cloudinaryCloudName,
                                    isDefault = imageId == defaultImageId,
                                    onClick = { onDefaultImageIdChange(imageId) }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val result = fileChooser.showOpenDialog(null)
                            if (result == JFileChooser.APPROVE_OPTION) {
                                onLocalFilesSelected(fileChooser.selectedFiles.toList())
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Upload,
                            contentDescription = "Upload",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Select New Images to Upload")
                    }

                    if (selectedLocalFiles.isNotEmpty()) {
                        Column(modifier = Modifier.padding(top = 12.dp, start = 8.dp, end = 8.dp)) {
                            Text(
                                "Staged for upload:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            selectedLocalFiles.forEach { file ->
                                Text("• ${file.name}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }


            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onNavigateBack, enabled = !isSaving) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(onClick = onSaveProduct, enabled = !isSaving && !isLoading) {
                        Text(if (isEditMode) "Update Product" else "Add Product")
                    }
                }
            }
        }

        if (isLoading || isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}


@Composable
private fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ImageCard(
    imageId: String,
    cloudinaryCloudName: String,
    isDefault: Boolean,
    onClick: () -> Unit
) {
    val imageUrl =
        "https://res.cloudinary.com/$cloudinaryCloudName/image/upload/w_150,h_150,c_fill/$imageId"
    val borderColor =
        if (isDefault) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Card(
        onClick = onClick,
        modifier = Modifier.size(150.dp),
        border = BorderStroke(if (isDefault) 3.dp else 1.dp, borderColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = imageId,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (isDefault) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Default Image",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }
    }
}
