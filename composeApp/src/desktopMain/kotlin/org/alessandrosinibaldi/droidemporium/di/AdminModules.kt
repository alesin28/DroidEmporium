package org.alessandrosinibaldi.droidemporium.di

import org.alessandrosinibaldi.droidemporium.adminCategory.data.FirestoreCategoryRepository
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.adminCategory.presentation.CategoryListViewModel
import org.alessandrosinibaldi.droidemporium.adminClient.data.FirestoreClientRepository
import org.alessandrosinibaldi.droidemporium.adminClient.domain.ClientRepository
import org.alessandrosinibaldi.droidemporium.adminClient.presentation.ClientListViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.data.FirestoreProductRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductDetailViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductFormViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module


val appModule = module {


    singleOf(::FirestoreProductRepository).bind<ProductRepository>()
    singleOf(::FirestoreCategoryRepository).bind<CategoryRepository>()
    singleOf(::FirestoreClientRepository).bind<ClientRepository>()

    viewModelOf(::ClientListViewModel)

    viewModel { params ->
        ProductListViewModel(
            productRepository = get(),
            categoryRepository = get(),
        )
    }

    viewModel { params ->
        ProductDetailViewModel(
            productRepository = get(),
            categoryRepository = get(),
            productId = params.getOrNull()
        )
    }

    viewModel { params ->
        ProductFormViewModel(
            productRepository = get(),
            categoryRepository = get(),
            productId = params.getOrNull()
        )
    }

    viewModel { params ->
        CategoryListViewModel(
            productRepository = get(),
            categoryRepository = get()
        )
    }
}

