package org.alessandrosinibaldi.droidemporium.adminReview.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import org.alessandrosinibaldi.droidemporium.adminReview.domain.AdminReviewRepository
import org.alessandrosinibaldi.droidemporium.commonReview.data.dto.ReviewDto
import org.alessandrosinibaldi.droidemporium.commonReview.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class AdminFirestoreReviewRepository: AdminReviewRepository {
    private val firestore = Firebase.firestore
    private val reviewsCollection = firestore.collection("reviews")

    override suspend fun getAllReviews(): Result<List<Review>> {
        return try {
            val querySnapshot = reviewsCollection.get()
            val reviews = querySnapshot.documents.map { doc ->
                doc.data<ReviewDto>().toDomain(id = doc.id)
            }
            Result.Success(reviews)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId).delete()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getReviewsForProduct(productId: String): Result<List<Review>> {
        return try {
            val querySnapshot = reviewsCollection.where { "productId" equalTo productId }.get()
            val reviews = querySnapshot.documents.map { doc ->
                doc.data<ReviewDto>().toDomain(id = doc.id)
            }
            Result.Success(reviews)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getReviewsByClient(clientId: String): Result<List<Review>> {
        return try {
            val querySnapshot = reviewsCollection.where { "clientId" equalTo clientId }.get()
            val reviews = querySnapshot.documents.map { doc ->
                doc.data<ReviewDto>().toDomain(id = doc.id)
            }
            Result.Success(reviews)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

}