package com.movie.rate.domain.valueobjects

import kotlin.text.isNotBlank
import kotlin.text.toRegex

@JvmInline
value class Email(val value: String) {

    init {
        check(value.isNotBlank()) { "Email cannot be blank" }
        check(isValidEmail(value)) { "Invalid email format: $value" }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    override fun toString(): String = value
}
