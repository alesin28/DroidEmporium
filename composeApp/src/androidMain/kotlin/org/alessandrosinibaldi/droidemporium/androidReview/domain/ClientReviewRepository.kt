package org.alessandrosinibaldi.droidemporium.androidReview.domain

import org.alessandrosinibaldi.droidemporium.commonReview.domain.ReviewRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result


interface ClientReviewRepository : ReviewRepository {

    suspend fun addReview(
        productId: String,
        clientId: String,
        clientDisplayName: String,
        rating: Int,
        content: String
    ): Result<Unit>

    suspend fun updateReview(reviewId: String, newRating: Int, newContent: String): Result<Unit>

    suspend fun hasUserReviewedProduct(clientId: String, productId: String): Result<Boolean>

}