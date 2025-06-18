package org.alessandrosinibaldi.droidemporium.adminReview.domain

import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.commonReview.domain.ReviewRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface AdminReviewRepository : ReviewRepository {
    suspend fun getAllReviews(): Result<List<Review>>
}