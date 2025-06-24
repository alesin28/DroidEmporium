package org.alessandrosinibaldi.droidemporium.di

import org.alessandrosinibaldi.droidemporium.commonCategory.data.FirestoreCategoryRepository
import org.alessandrosinibaldi.droidemporium.adminCategory.presentation.CategoryListViewModel
import org.alessandrosinibaldi.droidemporium.adminClient.data.FirestoreClientRepository
import org.alessandrosinibaldi.droidemporium.adminClient.domain.AdminClientRepository
import org.alessandrosinibaldi.droidemporium.adminClient.presentation.ClientListViewModel
import org.alessandrosinibaldi.droidemporium.adminOrder.data.AdminFirestoreOrderRepository
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.AdminOrderRepository
import org.alessandrosinibaldi.droidemporium.adminOrder.presentation.OrderDetailViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.data.AdminFirestoreProductRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductDetailViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductFormViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductListViewModel
import org.alessandrosinibaldi.droidemporium.adminOrder.presentation.OrderListViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.data.CloudinaryUploader
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.AdminProductRepository
import org.alessandrosinibaldi.droidemporium.adminReview.data.AdminFirestoreReviewRepository
import org.alessandrosinibaldi.droidemporium.adminReview.domain.AdminReviewRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.core.config.CloudinaryConfig
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


val desktopAppModule = module {

    single {
        val properties = Properties()
        val localPropertiesFile = File("cloudinary.properties")

        if (localPropertiesFile.exists()) {
            FileInputStream(localPropertiesFile).use { fis ->
                properties.load(fis)
            }
        } else {
            throw IllegalStateException("local.properties file not found in project root. Cannot start application.")
        }

        CloudinaryConfig(
            cloudName = properties.getProperty("CLOUDINARY_CLOUD_NAME")
                ?: throw IllegalStateException("CLOUDINARY_CLOUD_NAME not found in local.properties"),
            apiKey = properties.getProperty("CLOUDINARY_API_KEY")
                ?: throw IllegalStateException("CLOUDINARY_API_KEY not found in local.properties"),
            apiSecret = properties.getProperty("CLOUDINARY_API_SECRET")
                ?: throw IllegalStateException("CLOUDINARY_API_SECRET not found in local.properties"),
            preset = properties.getProperty("CLOUDINARY_PRESET_NAME")
                    ?: throw IllegalStateException("CLOUDINARY_PRESET_NAME not found in local.properties")
        )
    }

    single {
        HttpClient(CIO) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            install(Logging) {
                level = LogLevel.ALL
                logger = FileLogger()
            }
            expectSuccess = true
        }
    }

    single {
        CloudinaryUploader(
            client = get(),
            config = get()
        )
    }


    singleOf(::AdminFirestoreProductRepository).bind<AdminProductRepository>()
    singleOf(::FirestoreCategoryRepository).bind<CategoryRepository>()
    singleOf(::FirestoreClientRepository).bind<AdminClientRepository>()
    singleOf(::AdminFirestoreOrderRepository).bind<AdminOrderRepository>()
    singleOf(::AdminFirestoreReviewRepository).bind<AdminReviewRepository>()

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
            productId = params.getOrNull(),
            adminReviewRepository = get(),
            adminOrderRepository = get()
        )
    }

    viewModel { params ->
        ProductFormViewModel(
            adminProductRepository = get(),
            categoryRepository = get(),
            cloudinaryUploader = get(),
            cloudinaryConfig = get(),
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
            adminOrderRepository = get(),
            adminClientRepository = get(),
            orderId = params.getOrNull()
        )
    }

}

