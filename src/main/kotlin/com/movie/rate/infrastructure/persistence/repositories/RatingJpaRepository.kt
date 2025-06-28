package com.movie.rate.infrastructure.persistence.repositories

import com.movie.rate.infrastructure.entities.RatingJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RatingJpaRepository : JpaRepository<RatingJpaEntity, Long> {
    fun findByUserUuidAndMovieUuid(
        userUuid: UUID,
        movieUuid: UUID,
    ): RatingJpaEntity?

    fun findByUserUuid(userUuid: UUID): List<RatingJpaEntity>
}
