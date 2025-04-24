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
                    price = documentSnapshot.data<Product>().price.toDouble()
                )
            }
            emit(products)
        }
        //return products
    }

    override suspend fun deleteProduct(product: Product) {
        println("Deleting product: $product")
        firestore.collection("products").document(product.id.toString()).delete()
    }

    override suspend fun addProduct(name: String, description: String?, price: Double) {
        println("Adding product: $name, $description, $price")
        val ref = firestore.collection("products").add(Product(name = name, description = description.toString(), price = price))
        println("Product added with ID: ${ref.id}")
    }
}