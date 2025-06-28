package com.movie.rate.presentation.controller

import com.movie.rate.application.dto.RatingResponse
import com.movie.rate.application.usecases.CreateRatingUseCase
import com.movie.rate.application.usecases.GetUserRatingsUseCase
import com.movie.rate.presentation.dto.CreateRatingRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ratings")
@Tag(name = "Ratings", description = "Movie rating operations")
class RatingController(
    private val createRatingUseCase: CreateRatingUseCase,
    private val getUserRatingsUseCase: GetUserRatingsUseCase,
) {
    @PostMapping
    @Operation(summary = "Create or update a rating", description = "Creates a new rating or updates an existing one")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Rating created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "404", description = "User or movie not found"),
        ],
    )
    fun createRating(
        @Valid @RequestBody request: CreateRatingRequestDto,
    ): ResponseEntity<RatingResponse> {
        val ratingResponse = createRatingUseCase.execute(request.toApplicationDto())
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingResponse)
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user ratings", description = "Retrieves all ratings made by a specific user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Ratings retrieved successfully"),
            ApiResponse(responseCode = "404", description = "User not found"),
            ApiResponse(responseCode = "400", description = "Invalid user ID format"),
        ],
    )
    fun getUserRatings(
        @Parameter(description = "User unique identifier", required = true)
        @PathVariable userId: String,
    ): ResponseEntity<List<RatingResponse>> {
        val ratings = getUserRatingsUseCase.execute(userId)
        return ResponseEntity.ok(ratings)
    }
}
