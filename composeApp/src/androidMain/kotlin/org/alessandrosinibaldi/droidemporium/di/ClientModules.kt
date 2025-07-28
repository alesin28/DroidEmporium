package org.alessandrosinibaldi.droidemporium.di

import org.alessandrosinibaldi.droidemporium.androidAuth.data.FirestoreAuthRepository
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.androidAuth.presentation.AuthViewModel
import org.alessandrosinibaldi.droidemporium.androidCart.data.FirestoreCartRepository
import org.alessandrosinibaldi.droidemporium.androidProduct.data.ClientFirestoreProductRepository
import org.alessandrosinibaldi.droidemporium.androidProduct.domain.ClientProductRepository
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.data.FirestoreCategoryRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.home.presentation.HomeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val androidAppModule = module {

    singleOf(::FirestoreCartRepository).bind<CartRepository>()
    singleOf(::ClientFirestoreProductRepository).bind<ClientProductRepository>()
    singleOf(::FirestoreAuthRepository).bind<AuthRepository>()
    singleOf(::FirestoreCategoryRepository).bind<CategoryRepository>()

    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
}