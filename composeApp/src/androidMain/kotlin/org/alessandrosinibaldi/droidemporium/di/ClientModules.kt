package org.alessandrosinibaldi.droidemporium.di

import org.alessandrosinibaldi.droidemporium.androidCart.data.FirestoreCartRepository
import org.alessandrosinibaldi.droidemporium.androidProduct.data.ClientFirestoreProductRepository
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartRepository
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.ProductRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val androidAppModule = module {

    singleOf(::FirestoreCartRepository).bind<CartRepository>()
    singleOf(::ClientFirestoreProductRepository).bind<ProductRepository>()

}