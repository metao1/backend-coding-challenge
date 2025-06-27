package com.movie.rate.domain.repositories

import com.movie.rate.application.domain.valueobjects.Email
import com.movie.rate.application.domain.valueobjects.UserId
import com.movie.rate.domain.entities.User

interface UserRepository {
    fun save(user: User): User
    fun findById(id: UserId): User?
    fun existsByEmail(email: Email): Boolean
    fun existsByUsername(username: String): Boolean
}
