package org.alessandrosinibaldi.droidemporium.adminCategory.domain

import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun searchCategories(query: String? = null): Flow<List<Category>>
    suspend fun getCategoryById(id: String): Category?


}