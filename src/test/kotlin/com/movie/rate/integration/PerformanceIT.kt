package com.movie.rate.integration

import com.movie.rate.integration.Constants.Companion.MOVIES_ENDPOINT
import com.movie.rate.integration.Constants.Companion.USERS_ENDPOINT
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.lessThan

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class PerformanceIT : BaseIntegrationTest() {

    @Test
    fun `should handle 20 concurrent user creation requests successfully`() {
        val executor = Executors.newFixedThreadPool(10)
        val futures = mutableListOf<CompletableFuture<Int>>()

        // Create 20 concurrent requests
        repeat(20) { index ->
            val future = CompletableFuture.supplyAsync({
                val requestBody: String = """
                    {
                        "email": "concurrent.user$index@example.com",
                        "username": "concurrentuser$index",
                        "full_name": "Concurrent User $index"
                    }
                """.trimIndent()

                given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .`when`()
                    .post(USERS_ENDPOINT)
                    .then()
                    .extract()
                    .statusCode()
            }, executor)
            futures.add(future)
        }

        // Wait for all requests to complete
        val results = futures.map { it.get() }

        // All requests should succeed
        assertTrue(results.all { it == HttpStatus.CREATED.value() }, "All concurrent requests should succeed")

        executor.shutdown()
    }

    @Test
    fun `should handle 10 concurrent movie creation requests successfully`() {
        val executor = Executors.newFixedThreadPool(5)
        val futures = mutableListOf<CompletableFuture<Int>>()

        repeat(10) { index ->
            val future = CompletableFuture.supplyAsync({
                val requestBody = """
                    {
                        "title": "Concurrent Movie $index",
                        "description": "A movie created during concurrent testing scenario number $index",
                        "release_date": "2023-01-01",
                        "genre": "Action",
                        "director": "Concurrent Director $index"
                    }
                """.trimIndent()

                given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .`when`()
                    .post(MOVIES_ENDPOINT)
                    .then()
                    .extract()
                    .statusCode()
            }, executor)
            futures.add(future)
        }

        val results = futures.map { it.get() }
        assertTrue(results.all { it == HttpStatus.CREATED.value() }, "All concurrent movie creation requests should succeed")

        executor.shutdown()
    }

    @RepeatedTest(5)
    fun `should consistently return movies list within 2 seconds`() {
        // First ensure we have some movies by creating them if needed
        val currentMoviesResponse = given()
            .`when`()
            .get(MOVIES_ENDPOINT)
            .then()
            .statusCode(200)
            .extract()
            .path<Int>("total_elements")

        // If we don't have enough movies, create some
        if (currentMoviesResponse < 5) {
            repeat(5 - currentMoviesResponse) { index ->
                val movieRequest = """
                    {
                        "title": "Performance Test Movie ${System.currentTimeMillis()}-$index",
                        "description": "A test movie for performance testing",
                        "release_date": "2024-01-01",
                        "genre": "Action",
                        "director": "Test Director"
                    }
                """.trimIndent()

                given()
                    .contentType(ContentType.JSON)
                    .body(movieRequest)
                    .`when`()
                    .post(MOVIES_ENDPOINT)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
            }
        }

        // Now test the performance
        given()
            .`when`()
            .get(MOVIES_ENDPOINT)
            .then()
            .statusCode(200)
            .body("movies", hasSize<Any>(greaterThanOrEqualTo(5)))
            .body("total_elements", greaterThanOrEqualTo(5))
            .time(lessThan(2000L)) // Should respond within 2 seconds
    }

    @Test
    fun `should handle 50 rapid sequential requests within 10 seconds`() {
        val startTime = System.currentTimeMillis()

        repeat(50) {
            given()
                .`when`()
                .get(MOVIES_ENDPOINT)
                .then()
                .statusCode(200)
        }

        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime

        // 50 requests should complete within 10 seconds
        assertTrue(totalTime < 10000, "50 sequential requests should complete within 10 seconds, took ${totalTime}ms")
    }
}
