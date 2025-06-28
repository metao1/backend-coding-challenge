package com.movie.rate.infrastructure.persistence.repositories

import com.movie.rate.infrastructure.entities.MovieJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MovieJpaRepository : JpaRepository<MovieJpaEntity, Long> {
    fun findByUuid(uuid: UUID): MovieJpaEntity?

    override fun findAll(pageable: Pageable): Page<MovieJpaEntity>

    fun existsByTitle(title: String): Boolean
}
