package org.alessandrosinibaldi.droidemporium.androidReview.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import org.alessandrosinibaldi.droidemporium.androidReview.domain.ClientReviewRepository
import org.alessandrosinibaldi.droidemporium.commonReview.data.dto.ReviewDto
import org.alessandrosinibaldi.droidemporium.commonReview.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.core.domain.Result


class ClientFirestoreReviewRepository : ClientReviewRepository {

    private val firestore = Firebase.firestore
    private val reviewsCollection = firestore.collection("reviews")

    override suspend fun addReview(
        productId: String,
        clientId: String,
        clientDisplayName: String,
        rating: Int,
        content: String
    ): Result<Unit> {
        return try {
            val reviewDto = ReviewDto(
                productId = productId,
                clientId = clientId,
                clientDisplayName = clientDisplayName,
                rating = rating,
                content = content,
                reviewDate = Timestamp.now()
            )
            reviewsCollection.add(reviewDto)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun updateReview(reviewId: String, newRating: Int, newContent: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId).update(
                "rating" to newRating,
                "content" to newContent,
                "reviewDate" to Timestamp.now()
            )
            Result.Success(Unit)
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

    override suspend fun hasUserReviewedProduct(clientId: String, productId: String): Result<Boolean> {
        return try {
            val querySnapshot = reviewsCollection
                .where { "clientId" equalTo clientId }
                .where { "productId" equalTo productId }
                .limit(1)
                .get()

            Result.Success(querySnapshot.documents.isNotEmpty())
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}