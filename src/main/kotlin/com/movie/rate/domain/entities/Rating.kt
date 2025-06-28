package com.movie.rate.domain.entities

import com.movie.rate.domain.valueobjects.MovieId
import com.movie.rate.domain.valueobjects.RatingValue
import com.movie.rate.domain.valueobjects.UserId
import java.time.LocalDateTime

@ExposedCopyVisibility
data class Rating private constructor(
    val userId: UserId,
    val movieId: MovieId,
    private var _value: RatingValue,
    private var _comment: String?,
    private var _createdAt: LocalDateTime = LocalDateTime.now(),
    private var _updatedAt: LocalDateTime? = null,
) {
    val value: RatingValue get() = _value
    val comment: String? get() = _comment
    val createdAt: LocalDateTime get() = _createdAt
    val updatedAt: LocalDateTime? get() = _updatedAt

    companion object {
        fun create(
            userId: UserId,
            movieId: MovieId,
            value: RatingValue,
            comment: String?,
        ): Rating = Rating(userId, movieId, value, comment)

        fun fromPersistence(
            userId: UserId,
            movieId: MovieId,
            value: RatingValue,
            comment: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime?,
        ): Rating = Rating(userId, movieId, value, comment, createdAt, updatedAt)
    }

    fun updateRating(
        newValue: RatingValue,
        newComment: String?,
    ) {
        _value = newValue
        _comment = newComment
        _updatedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            other !is Rating -> false
            else -> userId == other.userId && movieId == other.movieId
        }

    override fun hashCode(): Int = 31 * userId.hashCode() + movieId.hashCode()

    override fun toString(): String = "Rating(userId=$userId, movieId=$movieId, value=$value, comment='$comment')"
}
