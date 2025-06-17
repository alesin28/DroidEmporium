package org.alessandrosinibaldi.droidemporium.adminCategory.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.alessandrosinibaldi.droidemporium.adminCategory.data.dto.CategoryDto
import org.alessandrosinibaldi.droidemporium.adminCategory.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result


class FirestoreCategoryRepository : CategoryRepository {

    private val firestore = Firebase.firestore
    private val categoriesCollection = firestore.collection("categories")

    override fun searchCategories(query: String): Flow<Result<List<Category>>> = flow {
        try {

            categoriesCollection.snapshots.collect { querySnapshot ->
                val allCategories = querySnapshot.documents.map { documentSnapshot ->
                    val dto = documentSnapshot.data<CategoryDto>()
                    dto.toDomain(id = documentSnapshot.id)
                }

                val filteredList = if (query.isBlank()) {
                    allCategories
                } else {
                    val lowerCaseQuery = query.lowercase()
                    allCategories.filter { category ->
                        category.name.lowercase().contains(lowerCaseQuery)
                    }
                }
                emit(Result.Success(filteredList))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }



    override suspend fun getCategoryById(id: String): Result<Category?> {
        return try {
            val snapshot = categoriesCollection.document(id).get()

            if (snapshot.exists) {
                val dto = snapshot.data<CategoryDto>()
                val category = dto.toDomain(id = snapshot.id)
                Result.Success(category)
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}