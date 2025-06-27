package com.movie.rate.application.usecases

import com.movie.rate.domain.valueobjects.MovieId
import com.movie.rate.domain.entities.Movie
import com.movie.rate.domain.exception.MovieNotFoundException
import com.movie.rate.domain.repositories.MovieRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime

class GetMovieUseCaseTest {

    private val movieRepository = mockk<MovieRepository>()
    private val getMovieUseCase = GetMovieUseCase(movieRepository)

    @Test
    fun `should get movie successfully when movie exists`() {
        // Given
        val movieId = MovieId.generate()
        val movieIdString = movieId.toString()
        val movie = Movie.fromPersistence(
            id = movieId,
            title = "The Matrix",
            description = "A computer hacker learns from mysterious rebels about the true nature of his reality.",
            releaseDate = LocalDate.of(1999, 3, 31),
            genre = "Science Fiction",
            director = "The Wachowskis",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        every { movieRepository.findById(movieId) } returns movie

        // When
        val result = getMovieUseCase.execute(movieIdString)

        // Then
        assertEquals(movieIdString, result.id)
        assertEquals("The Matrix", result.title)
        assertEquals("Science Fiction", result.genre)
        assertEquals("The Wachowskis", result.director)
        assertEquals(LocalDate.of(1999, 3, 31), result.releaseDate)
        assertNotNull(result.createdAt)

        verify { movieRepository.findById(movieId) }
    }

    @Test
    fun `should throw exception when movie does not exist`() {
        // Given
        val movieId = MovieId.generate()
        val movieIdString = movieId.toString()

        every { movieRepository.findById(movieId) } returns null

        // When & Then
        val exception = assertThrows<MovieNotFoundException> {
            getMovieUseCase.execute(movieIdString)
        }

        assertEquals("Movie with identifier '$movieIdString' not found", exception.message)
        verify { movieRepository.findById(movieId) }
    }

    @Test
    fun `should throw exception when movieId format is invalid`() {
        // Given
        val invalidMovieId = "invalid-uuid-format"

        // When & Then
        assertThrows<IllegalArgumentException> {
            getMovieUseCase.execute(invalidMovieId)
        }
    }

    @Test
    fun `should return movie with all fields populated`() {
        // Given
        val movieId = MovieId.generate()
        val movieIdString = movieId.toString()
        val movie = Movie.fromPersistence(
            id = movieId,
            title = "Inception",
            description = "A thief who steals corporate secrets through dream-sharing technology.",
            releaseDate = LocalDate.of(2010, 7, 16),
            genre = "Action",
            director = "Christopher Nolan",
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now()
        )

        every { movieRepository.findById(movieId) } returns movie

        // When
        val result = getMovieUseCase.execute(movieIdString)

        // Then
        assertEquals("Inception", result.title)
        assertEquals("Action", result.genre)
        assertEquals("Christopher Nolan", result.director)
        assertNotNull(result.updatedAt)

        verify { movieRepository.findById(movieId) }
    }
}
