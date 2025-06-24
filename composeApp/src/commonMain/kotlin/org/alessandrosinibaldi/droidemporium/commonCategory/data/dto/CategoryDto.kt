package org.alessandrosinibaldi.droidemporium.commonCategory.data.dto

import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category

@Serializable
data class CategoryDto(
    val name: String = ""
)

fun CategoryDto.toDomain(id: String): Category {
    return Category(
        id = id,
        name = this.name
    )
}