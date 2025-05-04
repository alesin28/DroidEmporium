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
            emit(categories)
        }

    }
}