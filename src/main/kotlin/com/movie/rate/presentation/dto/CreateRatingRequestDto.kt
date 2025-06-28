package com.movie.rate.presentation.dto

import com.movie.rate.application.dto.CreateRatingRequest
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateRatingRequestDto(
    @field:NotBlank(message = "User ID is required")
    val userId: String,
    @field:NotBlank(message = "Movie ID is required")
    val movieId: String,
    @field:NotNull(message = "Rating value is required")
    @field:Min(value = 1, message = "Rating must be at least 1")
    @field:Max(value = 5, message = "Rating must be at most 5")
    val value: Int,
    @field:Size(max = 1000, message = "Comment must not exceed 1000 characters")
    val comment: String?,
) {
    fun toApplicationDto(): CreateRatingRequest =
        CreateRatingRequest(
            userId = userId.trim(),
            movieId = movieId.trim(),
            value = value,
            comment = comment?.trim()?.takeIf { it.isNotBlank() },
        )
}
