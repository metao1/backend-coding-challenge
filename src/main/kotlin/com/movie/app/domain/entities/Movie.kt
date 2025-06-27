package com.movie.app.domain.entities

import com.movie.app.application.domain.valueobjects.MovieId
import java.time.LocalDate
import java.time.LocalDateTime

@ExposedCopyVisibility
data class Movie private constructor(
    val id: MovieId,
    val title: String,
    private var _description: String,
    val releaseDate: LocalDate,
    val genre: String,
    val director: String,
    private var _createdAt: LocalDateTime = LocalDateTime.now(),
    private var _updatedAt: LocalDateTime? = null
) {
    val description: String get() = _description
    val createdAt: LocalDateTime get() = _createdAt
    val updatedAt: LocalDateTime? get() = _updatedAt

    companion object {
        fun create(
            id: MovieId,
            title: String,
            description: String,
            releaseDate: LocalDate,
            genre: String,
            director: String
        ): Movie = Movie(id, title, description, releaseDate, genre, director)

        fun fromPersistence(
            id: MovieId,
            title: String,
            description: String,
            releaseDate: LocalDate,
            genre: String,
            director: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime?
        ): Movie = Movie(id, title, description, releaseDate, genre, director, createdAt, updatedAt)
    }

    fun updateDetails(newDescription: String) {
        _description = newDescription
        _updatedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Movie -> false
        else -> id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Movie(id=$id, title='$title', genre='$genre', director='$director', releaseDate=$releaseDate)"
}
