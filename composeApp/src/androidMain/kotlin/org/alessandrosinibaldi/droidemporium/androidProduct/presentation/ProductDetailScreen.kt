package org.alessandrosinibaldi.droidemporium.androidProduct.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.seiko.imageloader.rememberImagePainter
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.ui.theme.AccentOrange
import org.alessandrosinibaldi.droidemporium.ui.theme.DividerGray
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavHostController
) {
    val viewModel: ProductDetailViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )

    val product by viewModel.product.collectAsState()
    val category by viewModel.category.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    ProductDetailScreenContent(
        isLoading = isLoading,
        product = product,
        categoryName = category?.name,
        reviews = reviews,
        error = error,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreenContent(
    isLoading: Boolean,
    product: Product?,
    categoryName: String?,
    reviews: List<Review>,
    error: String?,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
            product != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        ImageGallery(
                            defaultImageId = product.defaultImageId,
                            allImageIds = product.imageIds
                        )
                        Spacer(Modifier.height(24.dp))
                    }

                    item {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = categoryName ?: "Uncategorized",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "€${"%.2f".format(product.price)}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        }
                        HorizontalDivider(Modifier.padding(vertical = 24.dp))
                    }

                    item {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                "Reviews (${reviews.size})",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    if (reviews.isEmpty()) {
                        item {
                            Text(
                                "No reviews yet.",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        items(reviews, key = { it.id }) { review ->
                            ReviewItem(
                                review = review,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageGallery(
    defaultImageId: String,
    allImageIds: List<String>
) {
    var selectedImageId by remember(defaultImageId) { mutableStateOf(defaultImageId) }
    val largeImageUrl = "https://res.cloudinary.com/dovupsygm/image/upload/w_800,c_fill/$selectedImageId"

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Image(
            painter = rememberImagePainter(largeImageUrl),
            contentDescription = "Selected product image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        if (allImageIds.size > 1) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allImageIds, key = { it }) { imageId ->
                    ThumbnailImage(
                        imageId = imageId,
                        isSelected = imageId == selectedImageId,
                        onClick = { selectedImageId = imageId }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThumbnailImage(
    imageId: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val imageUrl = "https://res.cloudinary.com/dovupsygm/image/upload/w_150,h_150,c_fill/$imageId"
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor), MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberImagePainter(imageUrl),
            contentDescription = "Thumbnail for image $imageId",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ReviewItem(review: Review, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                (1..5).forEach { starIndex ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (starIndex <= review.rating) AccentOrange else DividerGray
                    )
                }
            }
            Text(
                text = review.clientDisplayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = review.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        )
        HorizontalDivider(Modifier.padding(top = 16.dp))
    }
}