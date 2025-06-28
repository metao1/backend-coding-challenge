package com.movie.rate.application.usecases

import com.movie.rate.application.dto.CreateRatingRequest
import com.movie.rate.domain.exceptions.DuplicateRatingException
import com.movie.rate.infrastructure.entities.MovieJpaEntity
import com.movie.rate.infrastructure.entities.UserJpaEntity
import com.movie.rate.infrastructure.persistence.repositories.MovieJpaRepository
import com.movie.rate.infrastructure.persistence.repositories.RatingJpaRepository
import com.movie.rate.infrastructure.persistence.repositories.UserJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Test class to verify concurrency handling in the application.
 * Tests scenarios where multiple instances might try to create/update the same rating.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ConcurrencyTest {

    @Autowired
    private lateinit var createRatingUseCase: CreateRatingUseCase

    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    private lateinit var movieJpaRepository: MovieJpaRepository

    @Autowired
    private lateinit var ratingJpaRepository: RatingJpaRepository

    private lateinit var testUserId: String
    private lateinit var testMovieId: String

    @BeforeEach
    fun setUp() {
        // Clean up any existing data
        ratingJpaRepository.deleteAll()
        userJpaRepository.deleteAll()
        movieJpaRepository.deleteAll()

        // Create test user
        val userUuid = UUID.randomUUID()
        val user = UserJpaEntity(
            uuid = userUuid,
            email = "test@example.com",
            username = "testuser",
            fullName = "Test User",
            createdAt = LocalDateTime.now(),
            updatedAt = null,
        )
        userJpaRepository.save(user)
        testUserId = userUuid.toString()

        // Create test movie
        val movieUuid = UUID.randomUUID()
        val movie = MovieJpaEntity(
            uuid = movieUuid,
            title = "Test Movie",
            description = "A test movie",
            releaseDate = LocalDate.of(2023, 1, 1),
            genre = "Test",
            director = "Test Director",
            createdAt = LocalDateTime.now(),
            updatedAt = null,
        )
        movieJpaRepository.save(movie)
        testMovieId = movieUuid.toString()
    }

    @Test
    fun `should handle concurrent rating creation attempts gracefully`() {
        val request = CreateRatingRequest(
            userId = testUserId,
            movieId = testMovieId,
            value = 5,
            comment = "Great movie!",
        )

        val executor: ExecutorService = Executors.newFixedThreadPool(5)
        val futures = mutableListOf<CompletableFuture<Void>>()

        // Simulate 5 concurrent attempts to create the same rating
        repeat(5) {
            val future = CompletableFuture.runAsync({
                try {
                    createRatingUseCase.execute(request)
                } catch (ex: DuplicateRatingException) {
                    // Expected for concurrent attempts
                }
            }, executor)
            futures.add(future)
        }

        // Wait for all attempts to complete
        CompletableFuture.allOf(*futures.toTypedArray()).join()

        // Verify only one rating was created
        val ratings = ratingJpaRepository.findByUserUuid(UUID.fromString(testUserId))
        assertEquals(1, ratings.size, "Only one rating should be created despite concurrent attempts")

        executor.shutdown()
    }

    @Test
    fun `should throw DuplicateRatingException when trying to create duplicate rating`() {
        val request = CreateRatingRequest(
            userId = testUserId,
            movieId = testMovieId,
            value = 5,
            comment = "Great movie!",
        )

        // Create first rating
        createRatingUseCase.execute(request)

        // Attempt to create duplicate should throw exception
        assertThrows(DuplicateRatingException::class.java) {
            createRatingUseCase.execute(request)
        }
    }
}
