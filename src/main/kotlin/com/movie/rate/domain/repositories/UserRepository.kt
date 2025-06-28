package com.movie.rate.domain.repositories

import com.movie.rate.domain.entities.User
import com.movie.rate.domain.valueobjects.Email
import com.movie.rate.domain.valueobjects.UserId

interface UserRepository {
    fun save(user: User): User

    fun findById(id: UserId): User?

    fun existsByEmail(email: Email): Boolean

    fun existsByUsername(username: String): Boolean
}
