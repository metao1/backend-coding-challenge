package com.movie.rate.application.usecases

import com.movie.rate.application.dto.CreateMovieRequest
import com.movie.rate.domain.repositories.MovieRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CreateMovieUseCaseTest {

    private val movieRepository = mockk<MovieRepository>()
    private val createMovieUseCase = CreateMovieUseCase(movieRepository)

    @Test
    fun `should create movie successfully`() {
        // Given
        val request = CreateMovieRequest(
            title = "The Matrix",
            description = "A computer hacker learns from mysterious rebels about the true nature of his reality.",
            releaseDate = LocalDate.of(1999, 3, 31),
            genre = "Science Fiction",
            director = "The Wachowskis"
        )

        every { movieRepository.save(any()) } answers { firstArg() }

        // When
        val result = createMovieUseCase.execute(request)

        // Then
        assertNotNull(result.id)
        assertEquals(request.title, result.title)
        assertEquals(request.description, result.description)
        assertEquals(request.releaseDate, result.releaseDate)
        assertEquals(request.genre, result.genre)
        assertEquals(request.director, result.director)
        assertNotNull(result.createdAt)

        verify { movieRepository.save(any()) }
    }

    @Test
    fun `should create movie with all valid fields`() {
        // Given
        val request = CreateMovieRequest(
            title = "Inception",
            description = "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea.",
            releaseDate = LocalDate.of(2010, 7, 16),
            genre = "Action",
            director = "Christopher Nolan"
        )

        every { movieRepository.save(any()) } answers { firstArg() }

        // When
        val result = createMovieUseCase.execute(request)

        // Then
        assertEquals("Inception", result.title)
        assertEquals("Action", result.genre)
        assertEquals("Christopher Nolan", result.director)
        assertEquals(LocalDate.of(2010, 7, 16), result.releaseDate)

        verify { movieRepository.save(any()) }
    }

    @Test
    fun `should handle movie with long description`() {
        // Given
        val longDescription = "A".repeat(1000) // Long but valid description
        val request = CreateMovieRequest(
            title = "Test Movie",
            description = longDescription,
            releaseDate = LocalDate.of(2023, 1, 1),
            genre = "Drama",
            director = "Test Director"
        )

        every { movieRepository.save(any()) } answers { firstArg() }

        // When
        val result = createMovieUseCase.execute(request)

        // Then
        assertEquals(longDescription, result.description)
        verify { movieRepository.save(any()) }
    }
}
