package com.movie.rate.infrastructure.persistence.adapter

import com.movie.rate.domain.entities.User
import com.movie.rate.domain.repositories.UserRepository
import com.movie.rate.domain.valueobjects.Email
import com.movie.rate.domain.valueobjects.UserId
import com.movie.rate.infrastructure.entities.UserJpaEntity
import com.movie.rate.infrastructure.persistence.repositories.UserJpaRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    @CacheEvict(value = ["users"], allEntries = true)
    override fun save(user: User): User {
        val jpaEntity = UserJpaEntity.fromDomain(user)
        val savedEntity = userJpaRepository.save(jpaEntity)
        return savedEntity.toDomain()
    }

    @Cacheable("users")
    override fun findById(id: UserId): User? =
        Optional
            .ofNullable(userJpaRepository.findByUuid(id.value))
            .map { it.toDomain() }
            .orElse(null)

    override fun existsByEmail(email: Email): Boolean = userJpaRepository.existsByEmail(email.value)

    override fun existsByUsername(username: String): Boolean = userJpaRepository.existsByUsername(username)
}
