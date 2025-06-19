package org.alessandrosinibaldi.droidemporium.di

import org.alessandrosinibaldi.droidemporium.adminCategory.data.FirestoreCategoryRepository
import org.alessandrosinibaldi.droidemporium.adminCategory.presentation.CategoryListViewModel
import org.alessandrosinibaldi.droidemporium.adminClient.data.FirestoreClientRepository
import org.alessandrosinibaldi.droidemporium.adminClient.domain.AdminClientRepository
import org.alessandrosinibaldi.droidemporium.adminClient.presentation.ClientListViewModel
import org.alessandrosinibaldi.droidemporium.adminOrder.data.FirestoreOrderRepository
import org.alessandrosinibaldi.droidemporium.adminOrder.presentation.OrderDetailViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.data.AdminFirestoreProductRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductDetailViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductFormViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductListViewModel
import org.alessandrosinibaldi.droidemporium.adminOrder.presentation.OrderListViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.data.CloudinaryUploader
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.AdminProductRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.OrderRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module


val desktopAppModule = module {


    singleOf(::AdminFirestoreProductRepository).bind<AdminProductRepository>()
    singleOf(::FirestoreCategoryRepository).bind<CategoryRepository>()
    singleOf(::FirestoreClientRepository).bind<AdminClientRepository>()
    singleOf(::FirestoreOrderRepository).bind<OrderRepository>()

    single { CloudinaryUploader() }


    viewModelOf(::ClientListViewModel)

    viewModelOf(::OrderListViewModel)

    viewModel { params ->
        ProductListViewModel(
            adminProductRepository = get(),
            categoryRepository = get(),
        )
    }

    viewModel { params ->
        ProductDetailViewModel(
            adminProductRepository = get(),
            categoryRepository = get(),
            productId = params.getOrNull()
        )
    }

    viewModel { params ->
        ProductFormViewModel(
            adminProductRepository = get(),
            categoryRepository = get(),
            cloudinaryUploader = get(),
            productId = params.getOrNull()
        )
    }

    viewModel { params ->
        CategoryListViewModel(
            adminProductRepository = get(),
            categoryRepository = get()
        )
    }

    viewModel { params ->
        OrderDetailViewModel(
            orderRepository = get(),
            adminClientRepository = get(),
            orderId = params.getOrNull()
        )
    }

}

