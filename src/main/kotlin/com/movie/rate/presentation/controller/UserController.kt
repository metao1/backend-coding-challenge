package com.movie.rate.presentation.controller

import com.movie.rate.application.dto.CreateUserRequestDto
import com.movie.rate.application.dto.UserResponse
import com.movie.rate.application.usecases.CreateUserUseCase
import com.movie.rate.application.usecases.GetUserUseCase

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management operations")
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase
) {

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "409", description = "User already exists")
        ]
    )
    fun createUser(
        @Valid @RequestBody request: CreateUserRequestDto
    ): ResponseEntity<UserResponse> {
        val cleanRequest = request.toCleanRequest()
        val userResponse = createUserUseCase.execute(cleanRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse)
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User found"),
            ApiResponse(responseCode = "404", description = "User not found"),
            ApiResponse(responseCode = "400", description = "Invalid user ID format")
        ]
    )
    fun getUser(
        @Parameter(description = "User unique identifier", required = true)
        @PathVariable userId: String
    ): ResponseEntity<UserResponse> {
        val userResponse = getUserUseCase.execute(userId)
        return ResponseEntity.ok(userResponse)
    }
}
