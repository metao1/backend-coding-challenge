package com.movie.rate.infrastructure.persistence

import com.movie.rate.application.domain.valueobjects.MovieId
import com.movie.rate.application.domain.valueobjects.RatingValue
import com.movie.rate.application.domain.valueobjects.UserId
import com.movie.rate.domain.entities.Rating
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "ratings",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_uuid", "movie_uuid"])]
)
class RatingJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "user_uuid", nullable = false)
    val userUuid: UUID,

    @Column(name = "movie_uuid", nullable = false)
    val movieUuid: UUID,

    @Column(name = "rating_value", nullable = false)
    var value: Int,

    @Column(name = "comment", length = 1000)
    var comment: String?,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime?
) {
    companion object {
        fun fromDomain(rating: Rating): RatingJpaEntity = RatingJpaEntity(
            userUuid = rating.userId.value,
            movieUuid = rating.movieId.value,
            value = rating.value.value,
            comment = rating.comment,
            createdAt = rating.createdAt,
            updatedAt = rating.updatedAt
        )
    }

    fun toDomain(): Rating = Rating.fromPersistence(
        userId = UserId(userUuid),
        movieId = MovieId(movieUuid),
        value = RatingValue.of(value),
        comment = comment,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    // Best practices for JPA entities - use business identifier (userUuid + movieUuid)
    // This matches the unique constraint and business logic
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RatingJpaEntity) return false
        return userUuid == other.userUuid && movieUuid == other.movieUuid
    }

    override fun hashCode(): Int {
        var result = userUuid.hashCode()
        result = 31 * result + movieUuid.hashCode()
        return result
    }

    override fun toString(): String {
        return "RatingJpaEntity(id=$id, userUuid=$userUuid, movieUuid=$movieUuid, value=$value, comment='$comment')"
    }
}
