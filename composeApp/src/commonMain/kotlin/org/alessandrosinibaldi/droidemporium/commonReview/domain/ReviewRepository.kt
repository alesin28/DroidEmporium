package org.alessandrosinibaldi.droidemporium.commonReview.domain

import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface ReviewRepository {



    suspend fun deleteReview(reviewId: String): Result<Unit>

    suspend fun getReviewsForProduct(productId: String): Result<List<Review>>

    suspend fun getReviewsByClient(clientId: String): Result<List<Review>>



}