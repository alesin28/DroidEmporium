package org.alessandrosinibaldi.droidemporium.commonReview.data.dto

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import kotlinx.datetime.Instant

@Serializable
data class ReviewDto(
    val productId: String = "",
    val clientId: String = "",
    val rating: Int = 0,
    val content: String = "",
    val reviewDate: Timestamp? = null,
    val clientDisplayName: String = ""
)

fun ReviewDto.toDomain(id: String): Review {
    return Review(
        id = id,
        productId = this.productId,
        clientId = this.clientId,
        rating = this.rating,
        content = this.content,
        reviewDate = this.reviewDate?.toInstant() ?: Instant.DISTANT_PAST,
        clientDisplayName = this.clientDisplayName
    )
}

private fun Timestamp.toInstant(): Instant {
    return Instant.fromEpochSeconds(this.seconds, this.nanoseconds)
}