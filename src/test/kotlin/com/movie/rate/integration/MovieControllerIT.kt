package com.movie.rate.integration

import com.movie.rate.integration.Constants.Companion.MOVIES_ENDPOINT
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue

import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse.STATUS_BAD_REQUEST
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse.STATUS_NOT_FOUND
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse.STATUS_OK
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.test.annotation.DirtiesContext

/**
 * Integration tests for Movie Controller.
 * Tests basic movie CRUD operations in isolation:
 * - Movie creation and validation
 * - Movie retrieval by ID
 * - Movie listing with pagination
 * - Error handling for movie operations
 */
@DirtiesContext
class MovieControllerIT : BaseIntegrationTest() {

    companion object {

        // Default Test Values
        private const val DEFAULT_MOVIE_TITLE = "Integration Test Movie"
        private const val DEFAULT_MOVIE_DESCRIPTION = "A comprehensive test movie for integration testing"
        private const val DEFAULT_MOVIE_RELEASE_DATE = "2023-06-15"
        private const val DEFAULT_MOVIE_GENRE = "Science Fiction"
        private const val DEFAULT_MOVIE_DIRECTOR = "Test Director"

        // Pagination Constants
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private const val TEST_PAGE_SIZE = 5
        private const val DEFAULT_SORT_BY = "createdAt"
        private const val DEFAULT_SORT_DIRECTION = "DESC"

        // Test Data Suffixes
        private const val RETRIEVE_TEST_SUFFIX = "Retrieve Test Movie"
        private const val PAGINATION_TEST_SUFFIX = "Pagination Test Movie"
        private const val SIMPLE_GET_TEST_SUFFIX = "Simple GET Test Movie"

        // Error Messages
        private const val NOT_FOUND_ERROR = "Not Found"
        private const val NOT_FOUND_MESSAGE = "not found"

        // UUID Constants
        private const val NON_EXISTENT_UUID = "00000000-0000-0000-0000-000000000000"
    }

    // Movie-specific test data initialization
    private fun createTestMovie(
        title: String = "Movie Test ${System.currentTimeMillis()}",
        description: String = DEFAULT_MOVIE_DESCRIPTION,
        releaseDate: String = DEFAULT_MOVIE_RELEASE_DATE,
        genre: String = DEFAULT_MOVIE_GENRE,
        director: String = DEFAULT_MOVIE_DIRECTOR
    ): Map<String, Any> {
        return mapOf(
            "title" to title,
            "description" to description,
            "release_date" to releaseDate,
            "genre" to genre,
            "director" to director
        )
    }

    @Test
    fun `should create movie with valid data`() {
        val movieRequest = createTestMovie(
            title = DEFAULT_MOVIE_TITLE,
            description = DEFAULT_MOVIE_DESCRIPTION,
            releaseDate = DEFAULT_MOVIE_RELEASE_DATE,
            genre = DEFAULT_MOVIE_GENRE,
            director = DEFAULT_MOVIE_DIRECTOR
        )

        given()
            .contentType(ContentType.JSON)
            .body(movieRequest)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("title", equalTo(DEFAULT_MOVIE_TITLE))
            .body("description", containsString("comprehensive test movie"))
            .body("release_date", equalTo(DEFAULT_MOVIE_RELEASE_DATE))
            .body("genre", equalTo(DEFAULT_MOVIE_GENRE))
            .body("director", equalTo(DEFAULT_MOVIE_DIRECTOR))
            .body("id", notNullValue())
            .body("created_at", notNullValue())
    }

    @Test
    fun `should retrieve movie by id successfully`() {
        // Create a movie using isolated data
        val movieRequest = createTestMovie(
            title = RETRIEVE_TEST_SUFFIX,
            description = "A test movie for retrieval testing",
            releaseDate = "2024-01-01",
            genre = "Action",
            director = DEFAULT_MOVIE_DIRECTOR
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

        // Test retrieving the movie
        given()
            .`when`()
            .get("$MOVIES_ENDPOINT/$createdMovieId")
            .then()
            .statusCode(STATUS_OK)
            .body("id", equalTo(createdMovieId))
            .body("title", equalTo(movieRequest["title"]))
            .body("genre", equalTo(movieRequest["genre"]))
    }

    @Test
    fun `should retrieve movies with pagination`() {
        // Create a test movie using isolated data
        val movieRequest = createTestMovie(
            title = PAGINATION_TEST_SUFFIX,
            description = "A test movie for pagination testing",
            releaseDate = "2024-01-01",
            genre = "Comedy",
            director = DEFAULT_MOVIE_DIRECTOR
        )

        given()
            .contentType(ContentType.JSON)
            .body(movieRequest)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())

        // Test pagination using GET endpoint with query parameters
        given()
            .`when`()
            .get("$MOVIES_ENDPOINT?page=$DEFAULT_PAGE&size=$TEST_PAGE_SIZE&sortBy=$DEFAULT_SORT_BY&sortDirection=$DEFAULT_SORT_DIRECTION")
            .then()
            .statusCode(STATUS_OK)
            .body("movies", hasSize<Any>(greaterThanOrEqualTo(1))) // At least some movies
            .body("page", equalTo(DEFAULT_PAGE))
            .body("size", equalTo(TEST_PAGE_SIZE))
            .body("total_elements", greaterThanOrEqualTo(1))
            .body("has_previous", equalTo(false)) // First page should always have has_previous = false
            .body("movies[0]", notNullValue()) // Should have at least one movie
            .body("movies[0].id", notNullValue()) // Movie should have an ID
            .body("movies[0].title", notNullValue()) // Movie should have a title
    }

    @Test
    fun `should retrieve movies using simple GET endpoint`() {
        // Create a test movie
        val movieRequest = createTestMovie(
            title = SIMPLE_GET_TEST_SUFFIX,
            description = "A test movie for simple GET endpoint",
            releaseDate = "2024-01-01",
            genre = "Test",
            director = DEFAULT_MOVIE_DIRECTOR
        )

        given()
            .contentType(ContentType.JSON)
            .body(movieRequest)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())

        // Test simple GET endpoint with default parameters
        given()
            .`when`()
            .get(MOVIES_ENDPOINT)
            .then()
            .statusCode(STATUS_OK)
            .body("movies", hasSize<Any>(greaterThanOrEqualTo(1))) // At least some movies
            .body("page", equalTo(DEFAULT_PAGE))
            .body("size", equalTo(DEFAULT_SIZE)) // Default size
            .body("total_elements", greaterThanOrEqualTo(1))
            .body("has_previous", equalTo(false))
            .body("movies[0]", notNullValue())
    }

    @Test
    fun `should return validation errors for invalid movie input`() {
        // Create invalid movie data programmatically
        val invalidMovieRequest = mapOf(
            "title" to "", // Empty title
            "description" to "", // Empty description
            "release_date" to "invalid-date", // Invalid date format
            "genre" to "", // Empty genre
            "director" to "" // Empty director
        )

        val response = given()
            .contentType(ContentType.JSON)
            .body(invalidMovieRequest)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .extract()
            .response()

        // The API should return some error status (400 or 500)
        assert(response.statusCode >= STATUS_BAD_REQUEST) {
            "Expected error status (>=$STATUS_BAD_REQUEST), but got ${response.statusCode}. Response: ${response.asString()}"
        }
    }


    @Test
    fun `should return error for non-existent movie`() {
        given()
            .`when`()
            .get("$MOVIES_ENDPOINT/$NON_EXISTENT_UUID")
            .then()
            .statusCode(STATUS_NOT_FOUND)
            .body("status", equalTo(STATUS_NOT_FOUND))
            .body("error", equalTo(NOT_FOUND_ERROR))
            .body("message", containsString(NOT_FOUND_MESSAGE))
    }

    @Test
    fun `should handle pagination edge cases`() {
        // Create a test movie first
        val movieRequest = createTestMovie(
            title = "Pagination Edge Case Movie",
            description = "A test movie for pagination edge cases",
            releaseDate = "2024-01-01",
            genre = "Test",
            director = DEFAULT_MOVIE_DIRECTOR
        )

        given()
            .contentType(ContentType.JSON)
            .body(movieRequest)
            .`when`()
            .post(MOVIES_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())

        // Test page 0 (first page)
        given()
            .`when`()
            .get("$MOVIES_ENDPOINT?page=0&size=1")
            .then()
            .statusCode(STATUS_OK)
            .body("page", equalTo(0))
            .body("size", equalTo(1))
            .body("has_previous", equalTo(false))

        // Test large page size
        given()
            .`when`()
            .get("$MOVIES_ENDPOINT?page=0&size=100")
            .then()
            .statusCode(STATUS_OK)
            .body("page", equalTo(0))
            .body("size", equalTo(100))

        // Test empty page (beyond available data)
        given()
            .`when`()
            .get("$MOVIES_ENDPOINT?page=999&size=10")
            .then()
            .statusCode(STATUS_OK)
            .body("page", equalTo(999))
            .body("movies", hasSize<Any>(0))
            .body("has_next", equalTo(false))
    }
}
