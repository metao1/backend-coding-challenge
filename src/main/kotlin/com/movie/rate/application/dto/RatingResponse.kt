package com.movie.rate.application.dto

import com.movie.rate.domain.entities.Rating
import java.time.LocalDateTime

data class RatingResponse(
    val userId: String,
    val movieId: String,
    val value: Int,
    val comment: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun fromDomain(rating: Rating): RatingResponse {
            return RatingResponse(
                userId = rating.userId.toString(),
                movieId = rating.movieId.toString(),
                value = rating.value.value,
                comment = rating.comment,
                createdAt = rating.createdAt,
                updatedAt = rating.updatedAt
            )
        }
    }
}
