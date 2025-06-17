package org.alessandrosinibaldi.droidemporium.commonCategory.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface CategoryRepository {
    fun searchCategories(query: String): Flow<Result<List<Category>>>
    suspend fun getCategoryById(id: String): Result<Category?>


}