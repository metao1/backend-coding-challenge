package com.movie.rate.integration

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import kotlin.to

/**
 * Integration tests for Rating Controller.
 * Tests rating management operations with mocked user/movie dependencies:
 * - Rating creation with pre-existing user/movie
 * - Rating retrieval by user
 * - Rating validation
 * - Error handling for rating operations
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
class RatingControllerIT : BaseIntegrationTest() {

    companion object {
        // API Endpoints
        private const val USERS_ENDPOINT = "/api/users"
        private const val MOVIES_ENDPOINT = "/api/movies"
        private const val RATINGS_ENDPOINT = "/api/ratings"
    }

    // Rating-specific test data initialization
    private fun createTestRating(
        userId: String,
        movieId: String,
        value: Int = 5,
        comment: String = "Test rating comment for rating controller testing"
    ): Map<String, Any> {
        return mapOf(
            "user_id" to userId,
            "movie_id" to movieId,
            "value" to value,
            "comment" to comment
        )
    }

    // Helper method to create a user for rating tests (minimal setup)
    private fun createUserForRatingTest(): String {
        val userRequest = mapOf(
            "email" to "rating.user.${System.currentTimeMillis()}@example.com",
            "username" to "ratinguser${System.currentTimeMillis()}",
            "full_name" to "Rating User Test"
        )

        val userResponse = given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .`when`()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .extract()
            .response()

        return userResponse.path<String>("id")
    }

    // Helper method to create a movie for rating tests (minimal setup)
    private fun createMovieForRatingTest(): String {
        val movieRequest = mapOf(
            "title" to "Rating Movie Test ${System.currentTimeMillis()}",
            "description" to "A test movie for rating controller testing",
            "release_date" to "2024-01-01",
            "genre" to "Test",
            "director" to "Test Director"
        )

        val movieResponse = given()
            .contentType(ContentType.JSON)
            .body(movieRequest)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .extract()
            .response()

        return movieResponse.path<String>("id")
    }

    @Test
    fun `should create rating with valid data`() {
        // Setup: Create minimal user and movie
        val userId = createUserForRatingTest()
        val movieId = createMovieForRatingTest()

        // Test: Create rating
        val ratingRequest = createTestRating(
            userId = userId,
            movieId = movieId,
            value = 5,
            comment = "Excellent movie for rating controller testing!"
        )

        given()
            .contentType(ContentType.JSON)
            .body(ratingRequest)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .body("user_id", equalTo(userId))
            .body("movie_id", equalTo(movieId))
            .body("value", equalTo(5))
            .body("comment", containsString("Excellent movie"))
            .body("created_at", notNullValue())
    }

    @Test
    fun `should retrieve user ratings successfully`() {
        // Setup: Create minimal user and movie
        val userId = createUserForRatingTest()
        val movieId = createMovieForRatingTest()

        // Create a rating first
        val ratingRequest = createTestRating(
            userId = userId,
            movieId = movieId,
            value = 4,
            comment = "Good movie for rating retrieval testing"
        )

        given()
            .contentType(ContentType.JSON)
            .body(ratingRequest)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_CREATED)

        // Test: Retrieve user ratings
        given()
            .`when`()
            .get("$RATINGS_ENDPOINT/user/$userId")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$", hasSize<Any>(1))
            .body("[0].user_id", equalTo(userId))
            .body("[0].movie_id", equalTo(movieId))
            .body("[0].value", equalTo(4))
    }

    @Test
    fun `should return validation errors for invalid rating input`() {
        // Create invalid rating data programmatically
        val invalidRatingRequest = mapOf(
            "user_id" to "invalid-uuid", // Invalid UUID format
            "movie_id" to "", // Empty movie ID
            "value" to 11, // Invalid rating value (should be 1-10)
            "comment" to "" // Empty comment
        )

        given()
            .contentType(ContentType.JSON)
            .body(invalidRatingRequest)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("status", equalTo(400))
            .body("error", equalTo("Validation Failed"))
            .body("errors", notNullValue())
    }

    @Test
    fun `should return error for non-existent user in rating`() {
        // Setup: Create minimal movie
        val movieId = createMovieForRatingTest()
        val nonExistentUserId = "00000000-0000-0000-0000-000000000000"

        val ratingRequest = createTestRating(
            userId = nonExistentUserId,
            movieId = movieId,
            value = 5,
            comment = "Rating for non-existent user"
        )

        given()
            .contentType(ContentType.JSON)
            .body(ratingRequest)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .body("status", equalTo(404))
            .body("error", equalTo("Not Found"))
    }

    @Test
    fun `should return error for non-existent movie in rating`() {
        // Setup: Create minimal user
        val userId = createUserForRatingTest()
        val nonExistentMovieId = "00000000-0000-0000-0000-000000000000"

        val ratingRequest = createTestRating(
            userId = userId,
            movieId = nonExistentMovieId,
            value = 5,
            comment = "Rating for non-existent movie"
        )

        given()
            .contentType(ContentType.JSON)
            .body(ratingRequest)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(404)
            .body("status", equalTo(404))
            .body("error", equalTo("Not Found"))
    }

    @Test
    fun `should return empty list for user with no ratings`() {
        // Setup: Create minimal user (no ratings)
        val userId = createUserForRatingTest()

        // Test: Retrieve ratings for user with no ratings
        given()
            .`when`()
            .get("/api/ratings/user/$userId")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("$", hasSize<Any>(0)) // Should return empty array
    }

    @Test
    fun `should reject rating with invalid boundary values`() {
        // Setup: Create minimal user and movie
        val userId = createUserForRatingTest()
        val movieId = createMovieForRatingTest()

        // Test rating value 0 (below minimum)
        val ratingBelowMin = createTestRating(
            userId = userId,
            movieId = movieId,
            value = 0,
            comment = "Invalid rating below minimum"
        )

        given()
            .contentType(ContentType.JSON)
            .body(ratingBelowMin)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(400)
            .body("status", equalTo(400))

        // Test rating value 6 (above maximum)
        val ratingAboveMax = createTestRating(
            userId = userId,
            movieId = movieId,
            value = 6,
            comment = "Invalid rating above maximum"
        )

        given()
            .contentType(ContentType.JSON)
            .body(ratingAboveMax)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("status", equalTo(400))

        // Test negative rating value
        val negativeRating = createTestRating(
            userId = userId,
            movieId = movieId,
            value = -1,
            comment = "Invalid negative rating"
        )

        given()
            .contentType(ContentType.JSON)
            .body(negativeRating)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("status", equalTo(400))
    }

    @Test
    fun `should accept all valid rating boundary values`() {
        // Setup: Create minimal user and movie
        val userId = createUserForRatingTest()
        val movieId = createMovieForRatingTest()

        // Test minimum valid rating (1)
        val minRating = createTestRating(
            userId = userId,
            movieId = movieId,
            value = 1,
            comment = "Minimum valid rating"
        )

        given()
            .contentType(ContentType.JSON)
            .body(minRating)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .body("value", equalTo(1))

        // Update to maximum valid rating (5)
        val maxRating = createTestRating(
            userId = userId,
            movieId = movieId,
            value = 5,
            comment = "Maximum valid rating"
        )

        given()
            .contentType(ContentType.JSON)
            .body(maxRating)
            .`when`()
            .post(RATINGS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .body("value", equalTo(5))
    }
}
