package com.movie.rate.domain.entities

import com.movie.rate.application.domain.valueobjects.Email
import com.movie.rate.application.domain.valueobjects.UserId
import java.time.LocalDateTime

@ExposedCopyVisibility
data class User private constructor(
    val id: UserId,
    val email: Email,
    val username: String,
    private var _fullName: String,
    private var _createdAt: LocalDateTime = LocalDateTime.now(),
    private var _updatedAt: LocalDateTime? = null
) {
    val fullName: String get() = _fullName
    val createdAt: LocalDateTime get() = _createdAt
    val updatedAt: LocalDateTime? get() = _updatedAt

    companion object {
        fun create(
            id: UserId,
            email: Email,
            username: String,
            fullName: String
        ): User = User(id, email, username, fullName)

        fun fromPersistence(
            id: UserId,
            email: Email,
            username: String,
            fullName: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime?
        ): User = User(id, email, username, fullName, createdAt, updatedAt)
    }

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is User -> false
        else -> id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "User(id=$id, email=$email, username='$username', fullName='$fullName')"
}
