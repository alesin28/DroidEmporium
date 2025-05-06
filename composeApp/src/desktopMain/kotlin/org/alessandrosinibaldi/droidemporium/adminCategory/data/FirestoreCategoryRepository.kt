package org.alessandrosinibaldi.droidemporium.adminCategory.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.CategoryRepository

class FirestoreCategoryRepository : CategoryRepository {

    private val firestore = Firebase.firestore

    override fun searchCategories(query: String?) = flow {
        firestore.collection("categories").snapshots.collect { querySnapshot ->
            val categories = querySnapshot.documents.map { documentSnapshot ->

                Category(
                    id = documentSnapshot.id,
                    name = documentSnapshot.data<Category>().name
                )


            }
            val filteredCategories = if (!query.isNullOrBlank()) {
                val lowerCaseQuery = query.lowercase()
                categories.filter { category ->
                    category.name.lowercase().contains(lowerCaseQuery.toString())
                }
            } else {
                categories
            }
            emit(filteredCategories)
        }
    }



override suspend fun getCategoryById(id: String): Category? {
    val snapshot = firestore.collection("categories").document(id).get()
    return if (snapshot.exists) {
        snapshot.data<Category>().copy(id = snapshot.id)
    } else {
        null
    }
}
}