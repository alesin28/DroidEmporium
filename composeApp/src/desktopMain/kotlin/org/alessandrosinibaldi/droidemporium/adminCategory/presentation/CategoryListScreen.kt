package org.alessandrosinibaldi.droidemporium.adminCategory.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.alessandrosinibaldi.droidemporium.adminCategory.components.CategoryItem
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product


@Composable
fun CategoryListScreen(
    viewModel: CategoryListViewModel = koinViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    CategoryListScreenContent(
        categories = categories,
        products = products,
        searchQuery = searchQuery,
        onCategorySearch = viewModel::updateSearchQuery
    )
}

@Composable
fun CategoryListScreenContent(
    categories: List<Category>,
    products: List<Product>,
    searchQuery: String,
    onCategorySearch: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFADD8E6)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                CategorySearchBar(
                    onCategorySearch,
                    query = searchQuery
                )
                LazyColumn {
                    items(categories) { category ->
                        CategoryItem(
                            category = category,
                            products = products.filter { it.categoryId == category.id }
                        )
                    }
                }
            }


        }
    }
}

@Composable()
fun CategorySearchBar(onCategorySearch: (String) -> Unit, query: String) {
    var categoryQuery by remember { mutableStateOf(query) }

    OutlinedTextField(
        value = categoryQuery,
        onValueChange = { newQuery ->
            categoryQuery = newQuery

            onCategorySearch(categoryQuery)
        },
        label = { Text("Search") },
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 50.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)


    )
}