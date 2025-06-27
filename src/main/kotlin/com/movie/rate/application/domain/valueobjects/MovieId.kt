package com.movie.rate.application.domain.valueobjects

import java.util.UUID

@JvmInline
value class MovieId(val value: UUID) {

    companion object {
        fun generate(): MovieId = MovieId(UUID.randomUUID())

        fun fromString(value: String): MovieId {
            return try {
                MovieId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid MovieId format: $value", e)
            }
        }
    }

    override fun toString(): String = value.toString()
}
