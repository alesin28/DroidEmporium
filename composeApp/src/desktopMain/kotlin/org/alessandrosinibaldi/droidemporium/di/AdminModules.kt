package org.alessandrosinibaldi.droidemporium.di

import org.alessandrosinibaldi.droidemporium.adminProduct.data.FirestoreProductRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.AddProductViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf


val appModule = module {


    singleOf(::FirestoreProductRepository).bind<ProductRepository>()


    viewModelOf(::ProductListViewModel)
    viewModelOf(::AddProductViewModel)
}