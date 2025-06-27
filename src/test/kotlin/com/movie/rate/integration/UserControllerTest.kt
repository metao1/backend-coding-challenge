package com.movie.rate.integration

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest : BaseIntegrationTest() {

    companion object {
        // API Endpoints
        private const val USERS_ENDPOINT = "/api/users"

        // Default Test Values
        private const val DEFAULT_USER_EMAIL = "integration.test@example.com"
        private const val DEFAULT_USERNAME = "integrationtest"
        private const val DEFAULT_FULL_NAME = "Integration Test User"

        // HTTP Status Codes
        private const val STATUS_CREATED = 201
        private const val STATUS_BAD_REQUEST = 400
    }

    @Test
    fun `should create user with valid data`() {
        val userRequest = TestUtils.createTestUser(
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
            .statusCode(STATUS_CREATED)
            .body("email", equalTo(DEFAULT_USER_EMAIL))
            .body("username", equalTo(DEFAULT_USERNAME))
            .body("full_name", equalTo(DEFAULT_FULL_NAME))
            .body("id", notNullValue())
            .body("created_at", notNullValue())
    }

}
