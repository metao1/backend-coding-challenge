package com.movie.rate.infrastructure.persistence.repositories

import com.movie.rate.infrastructure.entities.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByUuid(uuid: UUID): UserJpaEntity?
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
}
