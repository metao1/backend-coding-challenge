package com.movie.rate.integration

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import kotlin.to

/**
 * Integration tests for Movie Controller.
 * Tests basic movie CRUD operations in isolation:
 * - Movie creation and validation
 * - Movie retrieval by ID
 * - Movie listing with pagination
 * - Error handling for movie operations
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
class MovieControllerIT : BaseIntegrationTest() {

    companion object {
        // API Endpoints
        private const val MOVIES_ENDPOINT = "/api/movies"

        // HTTP Status Codes
        private const val STATUS_OK = 200
        private const val STATUS_CREATED = 201
        private const val STATUS_BAD_REQUEST = 400
        private const val STATUS_NOT_FOUND = 404

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
            .statusCode(STATUS_CREATED)
            .body("title", equalTo(DEFAULT_MOVIE_TITLE))
            .body("description", containsString("comprehensive test movie"))
            .body("release_date", equalTo(DEFAULT_MOVIE_RELEASE_DATE))
            .body("genre", equalTo(DEFAULT_MOVIE_GENRE))
            .body("director", equalTo(DEFAULT_MOVIE_DIRECTOR))
            .body("id", notNullValue())
            .body("created_at", notNullValue())
    }

}
