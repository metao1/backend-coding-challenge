package com.movie.rate.application.usecases

import com.movie.rate.domain.entities.Movie
import com.movie.rate.domain.repositories.MovieRepository
import com.movie.rate.domain.repositories.PageRequest
import com.movie.rate.domain.repositories.PageResult
import com.movie.rate.domain.repositories.SortDirection
import com.movie.rate.domain.valueobjects.MovieId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class GetAllMoviesUseCaseTest {
    private val movieRepository = mockk<MovieRepository>()
    private val getAllMoviesUseCase = GetAllMoviesUseCase(movieRepository)

    @Test
    fun `should return paged movies response when movies exist`() {
        // Given
        val pageRequest =
            PageRequest(
                page = 0,
                size = 10,
                sortBy = "title",
                sortDirection = SortDirection.ASC,
            )

        val movie1 =
            Movie.create(
                id = MovieId.generate(),
                title = "Movie 1",
                description = "Description 1",
                releaseDate = LocalDate.of(2023, 1, 1),
                genre = "Action",
                director = "Director 1",
            )

        val movie2 =
            Movie.create(
                id = MovieId.generate(),
                title = "Movie 2",
                description = "Description 2",
                releaseDate = LocalDate.of(2023, 2, 1),
                genre = "Drama",
                director = "Director 2",
            )

        val pageResult =
            PageResult(
                content = listOf(movie1, movie2),
                page = 0,
                size = 10,
                totalElements = 2,
                totalPages = 1,
                hasNext = false,
                hasPrevious = false,
            )

        every { movieRepository.findAll(pageRequest) } returns pageResult

        // When
        val result = getAllMoviesUseCase.execute(pageRequest)

        // Then
        assertEquals(2, result.movies.size)
        assertEquals(0, result.page)
        assertEquals(10, result.size)
        assertEquals(2, result.totalElements)
        assertEquals(1, result.totalPages)
        assertEquals(false, result.hasNext)
        assertEquals(false, result.hasPrevious)

        assertEquals("Movie 1", result.movies[0].title)
        assertEquals("Movie 2", result.movies[1].title)

        verify { movieRepository.findAll(pageRequest) }
    }

    @Test
    fun `should return empty paged response when no movies exist`() {
        // Given
        val pageRequest =
            PageRequest(
                page = 0,
                size = 10,
                sortBy = "title",
                sortDirection = SortDirection.ASC,
            )

        val pageResult =
            PageResult(
                content = emptyList<Movie>(),
                page = 0,
                size = 10,
                totalElements = 0,
                totalPages = 0,
                hasNext = false,
                hasPrevious = false,
            )

        every { movieRepository.findAll(pageRequest) } returns pageResult

        // When
        val result = getAllMoviesUseCase.execute(pageRequest)

        // Then
        assertEquals(0, result.movies.size)
        assertEquals(0, result.totalElements)
        assertEquals(0, result.totalPages)

        verify { movieRepository.findAll(pageRequest) }
    }
}
