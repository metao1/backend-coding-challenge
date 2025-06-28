package com.movie.rate.application.dto

import java.time.LocalDate

data class CreateMovieRequest(
    val title: String,
    val description: String,
    val releaseDate: LocalDate,
    val genre: String,
    val director: String,
)
