package com.movie.rate.integration

import com.movie.rate.integration.Constants.Companion.USERS_ENDPOINT
import com.movie.rate.integration.TestUtils.Companion.createTestUser
import io.restassured.RestAssured.given
import io.restassured.http.ContentType

import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * Integration tests for User Controller.
 * Tests user management operations in isolation:
 * - User creation and validation
 * - User retrieval
 * - Error handling for user operations
 * - API contract validation for user endpoints
 */
class UserControllerIT : BaseIntegrationTest() {

    companion object {
        // Default Test Values
        private const val DEFAULT_USER_EMAIL = "integration.test@example.com"
        private const val DEFAULT_USERNAME = "integrationtest"
        private const val DEFAULT_FULL_NAME = "Integration Test User"

    }

    @Test
    fun `should create user with valid data`() {
        val userRequest = createTestUser(
            email = DEFAULT_USER_EMAIL,
            username = DEFAULT_USERNAME,
            fullName = DEFAULT_FULL_NAME
        )

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .`when`()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("email", equalTo(DEFAULT_USER_EMAIL))
            .body("username", equalTo(DEFAULT_USERNAME))
            .body("full_name", equalTo(DEFAULT_FULL_NAME))
            .body("id", notNullValue())
            .body("created_at", notNullValue())
    }

    @Test
    fun `should retrieve user by id successfully`() {
        // Create a user using isolated data
        val userRequest = createTestUser(
            email = "retrieve.test@example.com",
            username = "retrievetest",
            fullName = "Retrieve Test User"
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

        // Test retrieving the user
        given()
            .`when`()
            .get("/api/users/$createdUserId")
            .then()
            .statusCode(200)
            .body("id", equalTo(createdUserId))
            .body("email", equalTo(userRequest["email"]))
            .body("username", equalTo(userRequest["username"]))
            .body("full_name", equalTo(userRequest["full_name"]))
    }

    @Test
    fun `should accept valid JSON format for API contract validation`() {
        // JSON-based test for API contract validation
        val jsonPayload = """
            {
                "email": "contract.test@example.com",
                "username": "contracttest",
                "full_name": "Contract Test User"
            }
        """.trimIndent()

        given()
            .contentType(ContentType.JSON)
            .body(jsonPayload)
            .`when`()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("email", equalTo("contract.test@example.com"))
            .body("username", equalTo("contracttest"))
            .body("full_name", equalTo("Contract Test User"))
            .body("id", notNullValue())
            .body("created_at", notNullValue())
    }

    @Test
    fun `should return validation errors for invalid user input`() {
        // Create invalid user data programmatically
        val invalidUserRequest = mapOf(
            "email" to "invalid-email", // Invalid email format
            "username" to "", // Empty username
            "full_name" to "" // Empty full name
        )

        given()
            .contentType(ContentType.JSON)
            .body(invalidUserRequest)
            .`when`()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("error", equalTo("Validation Failed"))
            .body("errors", notNullValue())
            .body("errors.email", notNullValue())
            .body("errors.username", notNullValue())
            .body("errors.fullName", notNullValue())
    }

    @Test
    fun `should return error for non-existent user`() {
        val nonExistentUserId = "00000000-0000-0000-0000-000000000000"

        given()
            .`when`()
            .get("/api/users/$nonExistentUserId")
            .then()
            .statusCode(404)
            .body("status", equalTo(404))
            .body("error", equalTo("Not Found"))
            .body("message", containsString("not found"))
    }

    @Test
    fun `should return error for invalid UUID format`() {
        val invalidUserId = "invalid-uuid-format"

        given()
            .`when`()
            .get("/api/users/$invalidUserId")
            .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("error", equalTo("Bad Request"))
    }
}
