package com.movie.rate.domain.entities

import com.movie.rate.domain.valueobjects.MovieId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.text.contains

/**
 * Unit tests for Movie entity.
 * Tests movie creation and behavior without validation (handled at API layer).
 */
class MovieTest {
    @Test
    fun `should create movie with valid data`() {
        // Given
        val movieId = MovieId.generate()
        val title = "The Matrix"
        val description = "A computer hacker learns from mysterious rebels about the true nature of his reality."
        val releaseDate = LocalDate.of(1999, 3, 31)
        val genre = "Science Fiction"
        val director = "The Wachowskis"

        // When
        val movie = Movie.create(movieId, title, description, releaseDate, genre, director)

        // Then
        assertEquals(movieId, movie.id)
        assertEquals(title, movie.title)
        assertEquals(description, movie.description)
        assertEquals(releaseDate, movie.releaseDate)
        assertEquals(genre, movie.genre)
        assertEquals(director, movie.director)
        assertNotNull(movie.createdAt)
        assertNull(movie.updatedAt)
    }

    @Test
    fun `should update movie details`() {
        // Given
        val movie =
            Movie.create(
                MovieId.generate(),
                "The Matrix",
                "Original description",
                LocalDate.of(1999, 3, 31),
                "Science Fiction",
                "The Wachowskis",
            )
        val newDescription = "Updated description about the Matrix"

        // When
        movie.updateDetails(newDescription)

        // Then
        assertEquals(newDescription, movie.description)
        assertNotNull(movie.updatedAt)
    }

    @Test
    fun `should have correct equals and hashCode based on id`() {
        // Given
        val movieId = MovieId.generate()
        val movie1 = Movie.create(movieId, "Title1", "Desc1", LocalDate.now(), "Genre1", "Director1")
        val movie2 = Movie.create(movieId, "Title2", "Desc2", LocalDate.now(), "Genre2", "Director2")
        val movie3 = Movie.create(MovieId.generate(), "Title1", "Desc1", LocalDate.now(), "Genre1", "Director1")

        // Then
        assertEquals(movie1, movie2) // Same ID
        assertNotEquals(movie1, movie3) // Different ID
        assertEquals(movie1.hashCode(), movie2.hashCode()) // Same ID
        assertNotEquals(movie1.hashCode(), movie3.hashCode()) // Different ID
    }

    @Test
    fun `should have meaningful toString`() {
        // Given
        val movieId = MovieId.generate()
        val movie =
            Movie.create(
                movieId,
                "The Matrix",
                "A computer hacker learns from mysterious rebels.",
                LocalDate.of(1999, 3, 31),
                "Science Fiction",
                "The Wachowskis",
            )

        // When
        val toString = movie.toString()

        // Then
        assertTrue(toString.contains("The Matrix"))
        assertTrue(toString.contains("Science Fiction"))
        assertTrue(toString.contains("The Wachowskis"))
        assertTrue(toString.contains(movieId.toString()))
    }
}
