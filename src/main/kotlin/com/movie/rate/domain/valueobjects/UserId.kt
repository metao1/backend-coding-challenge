package com.movie.rate.domain.valueobjects

import java.util.UUID

@JvmInline
value class UserId(
    val value: UUID,
) {
    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID())

        fun fromString(value: String): UserId =
            try {
                UserId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw kotlin.IllegalArgumentException("Invalid UserId format: $value", e)
            }
    }

    override fun toString(): String = value.toString()
}
