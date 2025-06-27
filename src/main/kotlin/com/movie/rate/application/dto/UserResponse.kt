package com.movie.rate.application.dto

import com.movie.rate.domain.entities.User
import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val email: String,
    val username: String,
    val fullName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun fromDomain(user: User): UserResponse {
            return UserResponse(
                id = user.id.toString(),
                email = user.email.toString(),
                username = user.username,
                fullName = user.fullName,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
}
