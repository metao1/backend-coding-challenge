package com.movie.rate.integration

import com.movie.rate.integration.Constants.Companion.MOVIES_ENDPOINT
import com.movie.rate.integration.Constants.Companion.USERS_ENDPOINT

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext

/**
 * End-to-end integration tests for Movie Rating System workflows.
 * These tests verify complete business scenarios with isolated data:
 * - Complete rating workflows (User → Movie → Rating)
 * - Business logic scenarios (duplicate ratings, user rating history)
 * - Cross-domain interactions and data consistency
 *
 * Each test creates its own isolated data set to avoid interference.
 * Basic CRUD operations are tested in individual controller integration tests.
 */
@DirtiesContext
class MovieRatingSystemIT : BaseIntegrationTest() {

    private fun createTestRating(
        userId: String,
        movieId: String,
        value: Int = 5,
        comment: String = "Absolutely fantastic movie! Great storyline and excellent acting."
    ): Map<String, Any> {
        return mapOf(
            "user_id" to userId,
            "movie_id" to movieId,
            "value" to value,
            "comment" to comment
        )
    }

    @Test
    fun `should create rating with valid data`() {
        // Create a user using programmatic approach
        val userRequest = createTestUser(
            email = "rating.test@example.com",
            username = "ratingtest",
            fullName = "Rating Test User"
        )

        val userResponse = given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .`when`()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response()

        val createdUserId = userResponse.path<String>("id")

        // Create a movie using programmatic approach
        val movieRequest = createTestMovie(
            title = "Rating Test Movie",
            description = "A test movie for rating testing",
            releaseDate = "2024-01-01",
            genre = "Drama",
            director = "Test Director"
        )

        val movieResponse = given()
            .contentType(ContentType.JSON)
            .body(movieRequest)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response()

        val createdMovieId = movieResponse.path<String>("id")

        // Create rating using programmatic approach
        val ratingRequest = createTestRating(
            userId = createdUserId,
            movieId = createdMovieId,
            value = 5,
            comment = "Absolutely fantastic movie! Great storyline and excellent acting."
        )

        given()
            .contentType(ContentType.JSON)
            .body(ratingRequest)
            .`when`()
            .post("/api/ratings")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("user_id", equalTo(createdUserId))
            .body("movie_id", equalTo(createdMovieId))
            .body("value", equalTo(5))
            .body("comment", containsString("Absolutely fantastic movie"))
            .body("created_at", notNullValue())
    }

    @Test
    fun `should retrieve user ratings successfully`() {
        // Create a user using programmatic approach
        val userRequest = createTestUser(
            email = "userratings.test@example.com",
            username = "userratingstest",
            fullName = "User Ratings Test User"
        )

        val userResponse = given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .`when`()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response()

        val createdUserId = userResponse.path<String>("id")

        // Create a movie using programmatic approach
        val movieRequest = createTestMovie(
            title = "User Ratings Test Movie",
            description = "A test movie for user ratings testing",
            releaseDate = "2024-01-01",
            genre = "Thriller",
            director = "Test Director"
        )

        val movieResponse = given()
            .contentType(ContentType.JSON)
            .body(movieRequest)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response()

        val createdMovieId = movieResponse.path<String>("id")

        // Create rating using programmatic approach
        val ratingRequest = createTestRating(
            userId = createdUserId,
            movieId = createdMovieId,
            value = 5,
            comment = "Excellent movie for testing user ratings!"
        )

        given()
            .contentType(ContentType.JSON)
            .body(ratingRequest)
            .`when`()
            .post("/api/ratings")
            .then()
            .statusCode(HttpStatus.CREATED.value())

        // Test retrieving user ratings
        given()
            .`when`()
            .get("/api/ratings/user/$createdUserId")
            .then()
            .statusCode(200)
            .body("$", hasSize<Any>(1))
            .body("[0].user_id", equalTo(createdUserId))
            .body("[0].movie_id", equalTo(createdMovieId))
            .body("[0].value", equalTo(5))
    }

    @Test
    fun `should update existing rating when creating duplicate`() {
        // Create a user using programmatic approach
        val userRequest = createTestUser(
            email = "duplicate.test@example.com",
            username = "duplicatetest",
            fullName = "Duplicate Test User"
        )

        val userResponse = given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .`when`()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response()

        val createdUserId = userResponse.path<String>("id")

        // Create a movie using programmatic approach
        val movieRequest = createTestMovie(
            title = "Duplicate Test Movie",
            description = "A test movie for duplicate rating testing",
            releaseDate = "2024-01-01",
            genre = "Horror",
            director = "Test Director"
        )

        val movieResponse = given()
            .contentType(ContentType.JSON)
            .body(movieRequest)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response()

        val createdMovieId = movieResponse.path<String>("id")

        // Create initial rating using programmatic approach
        val initialRatingRequest = createTestRating(
            userId = createdUserId,
            movieId = createdMovieId,
            value = 5,
            comment = "Initial rating comment"
        )

        given()
            .contentType(ContentType.JSON)
            .body(initialRatingRequest)
            .`when`()
            .post("/api/ratings")
            .then()
            .statusCode(HttpStatus.CREATED.value())

        // Verify initial rating exists
        given()
            .`when`()
            .get("/api/ratings/user/$createdUserId")
            .then()
            .statusCode(200)
            .body("$", hasSize<Any>(1))
            .body("[0].value", equalTo(5))
            .body("[0].comment", equalTo("Initial rating comment"))

        // Create duplicate rating (should update existing) using programmatic approach
        val duplicateRatingRequest = createTestRating(
            userId = createdUserId,
            movieId = createdMovieId,
            value = 3,
            comment = "Updated rating comment"
        )

        // Try to create duplicate rating - this should update the existing one
        val duplicateResponse = given()
            .contentType(ContentType.JSON)
            .body(duplicateRatingRequest)
            .`when`()
            .post("/api/ratings")
            .then()
            .extract()
            .response()

        // Verify the duplicate rating was handled successfully
        assert(duplicateResponse.statusCode == HttpStatus.CREATED.value()) {
            "Expected status 201, but got ${duplicateResponse.statusCode}. Response: ${duplicateResponse.asString()}"
        }

        // Verify the response contains the updated values
        assert(duplicateResponse.path<String>("user_id") == createdUserId)
        assert(duplicateResponse.path<String>("movie_id") == createdMovieId)
        assert(duplicateResponse.path<Int>("value") == 3)
        assert(duplicateResponse.path<String>("comment") == "Updated rating comment")

        // Verify that there's still only one rating for this user-movie combination
        // and it has the updated values
        given()
            .`when`()
            .get("/api/ratings/user/$createdUserId")
            .then()
            .statusCode(200)
            .body("$", hasSize<Any>(1)) // Should still be only 1 rating
            .body("[0].value", equalTo(3)) // Should have the updated value
            .body("[0].comment", equalTo("Updated rating comment")) // Should have the updated comment
            .body("[0].created_at", notNullValue()) // Should have created timestamp
            .body("[0].updated_at", notNullValue()) // Should have updated timestamp
    }

    @Test
    fun `should display user profile with all rated movies`() {
        // Create a user for the profile test
        val userRequest = createTestUser(
            email = "profile.test@example.com",
            username = "profiletest",
            fullName = "Profile Test User"
        )

        val userResponse = given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .`when`()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response()

        val userId = userResponse.path<String>("id")

        // Create multiple movies for the user profile
        val movie1Request = createTestMovie(
            title = "Profile Movie 1",
            description = "First movie for profile testing",
            releaseDate = "2024-01-01",
            genre = "Action",
            director = "Director 1"
        )

        val movie1Response = given()
            .contentType(ContentType.JSON)
            .body(movie1Request)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response()

        val movie1Id = movie1Response.path<String>("id")

        val movie2Request = createTestMovie(
            title = "Profile Movie 2",
            description = "Second movie for profile testing",
            releaseDate = "2024-02-01",
            genre = "Comedy",
            director = "Director 2"
        )

        val movie2Response = given()
            .contentType(ContentType.JSON)
            .body(movie2Request)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response()

        val movie2Id = movie2Response.path<String>("id")

        // Rate both movies with different ratings (valid range: 1-5)
        val rating1Request = createTestRating(
            userId = userId,
            movieId = movie1Id,
            value = 5,
            comment = "Great action movie!"
        )

        given()
            .contentType(ContentType.JSON)
            .body(rating1Request)
            .`when`()
            .post("/api/ratings")
            .then()
            .statusCode(HttpStatus.CREATED.value())

        val rating2Request = createTestRating(
            userId = userId,
            movieId = movie2Id,
            value = 3,
            comment = "Decent comedy"
        )

        given()
            .contentType(ContentType.JSON)
            .body(rating2Request)
            .`when`()
            .post("/api/ratings")
            .then()
            .statusCode(HttpStatus.CREATED.value())

        // Test user profile: Get all movies rated by the user
        // This represents the user profile functionality where users can view their rated movies
        val profileResponse = given()
            .`when`()
            .get("/api/ratings/user/$userId")
            .then()
            .statusCode(200)
            .extract()
            .response()

        // Verify the profile contains both ratings
        val ratings = profileResponse.path<List<Map<String, Any>>>("$")
        assert(ratings.size == 2) { "Profile should contain 2 ratings, but found ${ratings.size}" }

        // Find and verify each rating
        val movie1Rating = ratings.find { it["movie_id"] == movie1Id }
        val movie2Rating = ratings.find { it["movie_id"] == movie2Id }

        assert(movie1Rating != null) { "Profile should contain rating for movie 1 ($movie1Id)" }
        assert(movie1Rating!!["value"] == 5) { "Movie 1 should have rating value 5" }
        assert(movie1Rating["comment"] == "Great action movie!") { "Movie 1 should have correct comment" }

        assert(movie2Rating != null) { "Profile should contain rating for movie 2 ($movie2Id)" }
        assert(movie2Rating!!["value"] == 3) { "Movie 2 should have rating value 3" }
        assert(movie2Rating["comment"] == "Decent comedy") { "Movie 2 should have correct comment" }

        // Verify that the user profile contains all necessary information
        val ratingsCount = ratings.size
        assert(ratingsCount == 2) { "User profile should show 2 rated movies, but found $ratingsCount" }

        // Verify that each rating in the profile contains complete information
        ratings.forEach { rating ->
            assert(rating.containsKey("movie_id")) { "Profile should contain movie_id for each rated movie" }
            assert(rating.containsKey("value")) { "Profile should contain rating value for each movie" }
            assert(rating.containsKey("comment")) { "Profile should contain user's comment for each movie" }
            assert(rating.containsKey("created_at")) { "Profile should contain rating date for each movie" }
        }
    }
}
