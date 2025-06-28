package com.movie.rate.application.dto

import com.movie.rate.domain.entities.Movie
import java.time.LocalDate
import java.time.LocalDateTime

data class MovieResponse(
    val id: String,
    val title: String,
    val description: String,
    val releaseDate: LocalDate,
    val genre: String,
    val director: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
) {
    companion object {
        fun fromDomain(movie: Movie): MovieResponse =
            MovieResponse(
                id = movie.id.toString(),
                title = movie.title,
                description = movie.description,
                releaseDate = movie.releaseDate,
                genre = movie.genre,
                director = movie.director,
                createdAt = movie.createdAt,
                updatedAt = movie.updatedAt,
            )
    }
}
