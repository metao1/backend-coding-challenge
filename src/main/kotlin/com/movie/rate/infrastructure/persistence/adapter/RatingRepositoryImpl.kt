package com.movie.rate.infrastructure.persistence.adapter

import com.movie.rate.domain.entities.Rating
import com.movie.rate.domain.repositories.RatingRepository
import com.movie.rate.domain.valueobjects.MovieId
import com.movie.rate.domain.valueobjects.UserId
import com.movie.rate.infrastructure.entities.RatingJpaEntity
import com.movie.rate.infrastructure.persistence.repositories.RatingJpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class RatingRepositoryImpl(
    private val ratingJpaRepository: RatingJpaRepository
) : RatingRepository {

    override fun save(rating: Rating): Rating {
        // Check if this is an update to an existing rating using Optional
        val jpaEntity = Optional.ofNullable(
            ratingJpaRepository.findByUserUuidAndMovieUuid(rating.userId.value, rating.movieId.value)
        ).map { existingEntity ->
            // Update existing entity to preserve the ID and avoid constraint violation
            existingEntity.apply {
                value = rating.value.value
                comment = rating.comment
                updatedAt = rating.updatedAt
            }
        }.orElse(
            // Create new entity
            RatingJpaEntity.fromDomain(rating)
        )

        val savedEntity = ratingJpaRepository.save(jpaEntity)
        return savedEntity.toDomain()
    }

    override fun findByUserIdAndMovieId(userId: UserId, movieId: MovieId): Rating? =
        Optional.ofNullable(ratingJpaRepository.findByUserUuidAndMovieUuid(userId.value, movieId.value))
            .map { it.toDomain() }
            .orElse(null)

    override fun findByUserId(userId: UserId): List<Rating> =
        ratingJpaRepository.findByUserUuid(userId.value).map { it.toDomain() }


}
