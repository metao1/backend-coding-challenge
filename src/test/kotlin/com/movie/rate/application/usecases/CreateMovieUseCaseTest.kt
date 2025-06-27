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

}
