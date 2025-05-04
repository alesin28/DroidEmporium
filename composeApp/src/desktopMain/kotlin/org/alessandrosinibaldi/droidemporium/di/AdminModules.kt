package org.alessandrosinibaldi.droidemporium.di

import org.alessandrosinibaldi.droidemporium.adminCategory.data.FirestoreCategoryRepository
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.data.FirestoreProductRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductFormViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module


val appModule = module {


    singleOf(::FirestoreProductRepository).bind<ProductRepository>()
    singleOf(::FirestoreCategoryRepository).bind<CategoryRepository>()

    //viewModelOf(::ProductListViewModel)

    viewModel { params ->
        ProductListViewModel(
            productRepository = get(),
            categoryRepository = get(),
        )
    }

    viewModel { params -> //
        ProductFormViewModel(
            productRepository = get(),
            categoryRepository = get(),
            productId = params.getOrNull()
        )
    }}