package org.alessandrosinibaldi.droidemporium.commonReview.domain

import kotlinx.datetime.Instant

data class Review(
    val id: String,
    val productId: String,
    val clientId: String,
    val rating: Int,
    val content: String,
    val reviewDate: Instant,
    val clientDisplayName: String
)