package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.seiko.imageloader.rememberImagePainter
import kotlinx.datetime.toJavaInstant
import org.alessandrosinibaldi.droidemporium.app.Route
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.core.components.MenuReturnButton
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProductDetailScreen(
    productId: String?,
    navController: NavHostController
) {
    val viewModel: ProductDetailViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )

    val product by viewModel.product.collectAsState()
    val category by viewModel.category.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val onNavigateToEdit = {
        if (productId != null) {
            navController.navigate(Route.ProductEdit(productId = productId))
        }
    }
    val onNavigateToOrderDetail: (String) -> Unit = { orderId ->
        navController.navigate(Route.OrderDetail(orderId = orderId))
    }

    ProductDetailScreenContent(
        product = product,
        category = category,
        reviews = reviews,
        orders = orders,
        isLoading = isLoading,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToEdit = onNavigateToEdit,
        onNavigateToOrderDetail = onNavigateToOrderDetail
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreenContent(
    product: Product?,
    category: Category?,
    reviews: List<Review>,
    orders: List<Order>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToOrderDetail: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (product == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Product not found.", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))
                    MenuReturnButton(onNavigateBack)
                }
            }
        } else {
            var selectedTabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("Reviews (${reviews.size})", "Orders (${orders.size})")
            var selectedImageId by remember(product.id) { mutableStateOf(product.defaultImageId) }
            val horizontalScrollState = rememberScrollState()

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MenuReturnButton(onNavigateBack)
                    Button(onClick = onNavigateToEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Product",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Edit Product")
                    }
                }
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(horizontalScrollState),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 500.dp)
                    ) {
                        item {
                            Text(
                                product.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                category?.name ?: "No Category",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.height(24.dp))
                        }
                        if (product.imageIds.isNotEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                                    shape = MaterialTheme.shapes.large,
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    val largeImageUrl =
                                        "https://res.cloudinary.com/dovupsygm/image/upload/w_800/$selectedImageId"
                                    Image(
                                        painter = rememberImagePainter(largeImageUrl),
                                        contentDescription = "Selected product image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                            }
                            item {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    product.imageIds.forEach { imageId ->
                                        ThumbnailImageCard(
                                            imageId = imageId,
                                            isSelected = imageId == selectedImageId,
                                            isDefault = imageId == product.defaultImageId,
                                            onClick = { selectedImageId = imageId }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(24.dp))
                            }
                        }

                        item {
                            Spacer(Modifier.height(24.dp))
                            DetailItem("Description", product.description)
                            DetailItem("Price", "€${product.price}")
                            DetailItem("Stock", "${product.stock}", isLowStock = product.stock < 10)
                            DetailItem(
                                "Status",
                                if (product.isActive) "Active" else "Inactive",
                                isStatus = true,
                                isActive = product.isActive
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .widthIn(min = 450.dp)
                    ) {
                        TabRow(selectedTabIndex = selectedTabIndex) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(title) }
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            when (selectedTabIndex) {
                                0 -> items(
                                    reviews,
                                    key = { it.id }) { review -> ReviewItem(review) }

                                1 -> items(orders, key = { it.id }) { order ->
                                    OrderItem(
                                        order = order,
                                        onClick = { onNavigateToOrderDetail(order.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    isLowStock: Boolean = false,
    isStatus: Boolean = false,
    isActive: Boolean = false
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val valueColor = when {
            isLowStock -> MaterialTheme.colorScheme.error
            isStatus && isActive -> MaterialTheme.colorScheme.primary
            isStatus && !isActive -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.onSurface
        }
        Text(value, style = MaterialTheme.typography.bodyLarge, color = valueColor)
    }
}

@Composable
private fun ImageCard(imageUrl: String, isDefault: Boolean) {
    Card(
        modifier = Modifier.size(150.dp),
        border = BorderStroke(
            if (isDefault) 3.dp else 1.dp,
            if (isDefault) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box {
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "Product Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (isDefault) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Default Image",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rating: ${review.rating}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text("by Client: ${review.clientId}", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(8.dp))
            Text(review.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrderItem(order: Order, onClick: () -> Unit) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    TooltipArea(
                        tooltip = {
                            Surface(
                                modifier = Modifier.shadow(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = order.id,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    ) {
                        Text(
                            text = "Order #${order.id}",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text("Client: ${order.clientId}", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "€${order.totalAmount}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        dateFormatter.format(Date.from(order.orderDate.toJavaInstant())),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ThumbnailImageCard(
    imageId: String,
    isSelected: Boolean,
    isDefault: Boolean,
    onClick: () -> Unit
) {
    val imageUrl = "https://res.cloudinary.com/dovupsygm/image/upload/w_150,h_150,c_fill/$imageId"

    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }

    val borderWidth = if (isSelected) 3.dp else 1.dp

    Card(
        onClick = onClick,
        modifier = Modifier.size(100.dp),
        border = BorderStroke(borderWidth, borderColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "Product thumbnail $imageId",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (isDefault) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Default Image",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .size(20.dp)
                )
            }
        }
    }
}