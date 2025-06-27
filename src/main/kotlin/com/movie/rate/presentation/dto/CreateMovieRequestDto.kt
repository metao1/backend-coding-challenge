package com.movie.rate.presentation.dto

import com.movie.rate.application.dto.CreateMovieRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateMovieRequestDto(
    @field:NotBlank(message = "Title is required")
    @field:Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    val title: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    val description: String,

    @field:NotNull(message = "Release date is required")
    @field:PastOrPresent(message = "Release date cannot be in the future")
    val releaseDate: LocalDate,

    @field:NotBlank(message = "Genre is required")
    @field:Size(min = 2, max = 50, message = "Genre must be between 2 and 50 characters")
    val genre: String,

    @field:NotBlank(message = "Director is required")
    @field:Size(min = 2, max = 100, message = "Director must be between 2 and 100 characters")
    val director: String
) {
    fun toApplicationDto(): CreateMovieRequest = CreateMovieRequest(
        title = title.trim(),
        description = description.trim(),
        releaseDate = releaseDate,
        genre = genre.trim(),
        director = director.trim()
    )
}
