package org.alessandrosinibaldi.droidemporium.core.utils

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant

fun Timestamp.toInstant(): Instant {
    return Instant.fromEpochSeconds(this.seconds, this.nanoseconds)
}