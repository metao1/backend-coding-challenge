package com.movie.rate.application.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequestDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,

    @field:NotBlank(message = "Full name is required")
    @field:Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    val fullName: String
) {
}
