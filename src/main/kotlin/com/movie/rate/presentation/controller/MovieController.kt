package com.movie.rate.presentation.controller

import com.movie.rate.application.dto.MovieResponse
import com.movie.rate.application.dto.PagedMoviesResponse
import com.movie.rate.application.usecases.CreateMovieUseCase
import com.movie.rate.application.usecases.GetAllMoviesUseCase
import com.movie.rate.application.usecases.GetMovieUseCase
import com.movie.rate.presentation.dto.CreateMovieRequestDto
import com.movie.rate.presentation.dto.PaginationRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movies", description = "Movie management operations")
@Transactional(readOnly = true)
class MovieController(
    private val createMovieUseCase: CreateMovieUseCase,
    private val getMovieUseCase: GetMovieUseCase,
    private val getAllMoviesUseCase: GetAllMoviesUseCase,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(MovieController::class.java)
    }

    @PostMapping
    @Operation(summary = "Create a new movie", description = "Creates a new movie with the provided information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Movie created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
        ],
    )
    @Transactional
    fun createMovie(
        @Valid @RequestBody request: CreateMovieRequestDto,
    ): ResponseEntity<MovieResponse> {
        logger.info("Creating movie with title: {}", request.title)
        val movieResponse = createMovieUseCase.execute(request.toApplicationDto())
        logger.info("Movie created successfully with ID: {}", movieResponse.id)
        return ResponseEntity.status(HttpStatus.CREATED).body(movieResponse)
    }

    @GetMapping("/{movieId}")
    @Operation(summary = "Get movie by ID", description = "Retrieves a movie by its unique identifier")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Movie found"),
            ApiResponse(responseCode = "404", description = "Movie not found"),
            ApiResponse(responseCode = "400", description = "Invalid movie ID format"),
        ],
    )
    fun getMovie(
        @Parameter(description = "Movie unique identifier", required = true)
        @PathVariable movieId: String,
    ): ResponseEntity<MovieResponse> {
        logger.debug("Retrieving movie with ID: {}", movieId)
        val movieResponse = getMovieUseCase.execute(movieId)
        return ResponseEntity.ok(movieResponse)
    }

    @GetMapping
    @Operation(summary = "Get all movies", description = "Retrieves all movies with pagination support")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Movies retrieved successfully"),
            ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        ],
    )
    fun getAllMovies(
        @Valid @ModelAttribute pagination: PaginationRequestDto,
    ): ResponseEntity<PagedMoviesResponse> {
        logger.debug("Retrieving movies with pagination: page={}, size={}", pagination.page, pagination.size)
        val pageRequest = pagination.toDomainPageRequest()
        val response = getAllMoviesUseCase.execute(pageRequest)
        logger.debug("Retrieved {} movies out of {} total", response.movies.size, response.totalElements)
        return ResponseEntity.ok(response)
    }
}
