package org.alessandrosinibaldi.droidemporium.adminReview.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.commonReview.domain.ReviewRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface AdminReviewRepository : ReviewRepository {
    fun getAllReviews(): Flow<Result<List<Review>>>
}