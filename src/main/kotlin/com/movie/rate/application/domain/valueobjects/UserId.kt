package com.movie.rate.application.domain.valueobjects

import java.util.UUID

@JvmInline
value class UserId(val value: UUID) {

    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID())
    }

    override fun toString(): String = value.toString()
}
