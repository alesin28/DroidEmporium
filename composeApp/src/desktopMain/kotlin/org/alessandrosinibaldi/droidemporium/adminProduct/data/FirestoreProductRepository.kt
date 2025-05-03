package org.alessandrosinibaldi.droidemporium.adminProduct.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository

class FirestoreProductRepository : ProductRepository {
    private val firestore = Firebase.firestore

    override fun searchProducts(query: String?) = flow {
        //var products = emptyList<Product>()
        firestore.collection("products").snapshots.collect { querySnapshot ->
            val products = querySnapshot.documents.map { documentSnapshot ->
                Product(
                    id = documentSnapshot.id,
                    name = documentSnapshot.data<Product>().name,
                    description = documentSnapshot.data<Product>().description,
                    price = documentSnapshot.data<Product>().price.toDouble(),
                    stock = documentSnapshot.data<Product>().stock.toInt(),
                    isActive = documentSnapshot.data<Product>().isActive
                )
            }

            val filteredProducts = if (!query.isNullOrBlank()) {
                val lowerCaseQuery = query.lowercase()
                products.filter { product ->
                    product.name.lowercase().contains(lowerCaseQuery.toString())
                }
            } else {
                products
            }

            emit(filteredProducts)
        }
        //return products
    }

    override suspend fun getProductById(id: String): Product? {
        val snapshot = firestore.collection("products").document(id).get()
        return if (snapshot.exists) {
            snapshot.data<Product>().copy(id = snapshot.id)
        } else {
            null
        }
    }

    override suspend fun updateProduct(product: Product) {
        firestore.collection("products").document(product.id.toString()).set(product, merge = false)
        println("Product updated: ${product.id}")
    }

    override suspend fun deleteProduct(product: Product) {
        println("Deleting product: $product")
        firestore.collection("products").document(product.id.toString()).delete()
    }

    override suspend fun addProduct(
        name: String,
        description: String?,
        price: Double,
        stock: Int,
        isActive: Boolean
    ) {
        println("Adding product: $name, $description, $price")
        val ref = firestore.collection("products")
            .add(
                Product(
                    name = name, description = description.toString(), price = price,
                    stock = stock,
                    isActive = isActive
                )
            )
        println("Product added with ID: ${ref.id}")
    }
}