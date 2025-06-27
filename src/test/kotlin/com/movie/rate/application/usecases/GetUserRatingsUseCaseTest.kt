package com.movie.rate.application.usecases

import com.movie.rate.application.domain.valueobjects.Email
import com.movie.rate.application.domain.valueobjects.MovieId
import com.movie.rate.application.domain.valueobjects.RatingValue
import com.movie.rate.application.domain.valueobjects.UserId
import com.movie.rate.domain.entities.Rating
import com.movie.rate.domain.entities.User
import com.movie.rate.domain.exception.UserNotFoundException
import com.movie.rate.domain.repositories.RatingRepository
import com.movie.rate.domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class GetUserRatingsUseCaseTest {

    private val ratingRepository = mockk<RatingRepository>()
    private val userRepository = mockk<UserRepository>()
    private val getUserRatingsUseCase = GetUserRatingsUseCase(ratingRepository, userRepository)

    @Test
    fun `should return user ratings when user exists and has ratings`() {
        // Given
        val userId = UserId.generate()
        val userIdString = userId.toString()
        val movieId1 = MovieId.generate()
        val movieId2 = MovieId.generate()

        val user = User.fromPersistence(
            id = userId,
            email = Email("john.doe@example.com"),
            username = "johndoe",
            fullName = "John Doe",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val rating1 = Rating.fromPersistence(
            userId = userId,
            movieId = movieId1,
            value = RatingValue.of(5),
            comment = "Excellent movie!",
            createdAt = LocalDateTime.now().minusDays(2),
            updatedAt = null
        )

        val rating2 = Rating.fromPersistence(
            userId = userId,
            movieId = movieId2,
            value = RatingValue.of(4),
            comment = "Very good!",
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = null
        )

        val ratings = listOf(rating1, rating2)

        every { userRepository.findById(userId) } returns user
        every { ratingRepository.findByUserId(userId) } returns ratings

        // When
        val result = getUserRatingsUseCase.execute(userIdString)

        // Then
        assertEquals(2, result.size)
        assertEquals(userIdString, result[0].userId)
        assertEquals(userIdString, result[1].userId)
        assertEquals(movieId1.toString(), result[0].movieId)
        assertEquals(movieId2.toString(), result[1].movieId)
        assertEquals(5, result[0].value)
        assertEquals(4, result[1].value)
        assertEquals("Excellent movie!", result[0].comment)
        assertEquals("Very good!", result[1].comment)

        verify { userRepository.findById(userId) }
        verify { ratingRepository.findByUserId(userId) }
    }

    @Test
    fun `should return empty list when user exists but has no ratings`() {
        // Given
        val userId = UserId.generate()
        val userIdString = userId.toString()

        val user = User.fromPersistence(
            id = userId,
            email = Email("john.doe@example.com"),
            username = "johndoe",
            fullName = "John Doe",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        every { userRepository.findById(userId) } returns user
        every { ratingRepository.findByUserId(userId) } returns emptyList()

        // When
        val result = getUserRatingsUseCase.execute(userIdString)

        // Then
        assertTrue(result.isEmpty())

        verify { userRepository.findById(userId) }
        verify { ratingRepository.findByUserId(userId) }
    }

    @Test
    fun `should throw exception when user does not exist`() {
        // Given
        val userId = UserId.generate()
        val userIdString = userId.toString()

        every { userRepository.findById(userId) } returns null

        // When & Then
        val exception = assertThrows<UserNotFoundException> {
            getUserRatingsUseCase.execute(userIdString)
        }

        assertEquals("User with identifier '$userIdString' not found", exception.message)
        verify { userRepository.findById(userId) }
        verify(exactly = 0) { ratingRepository.findByUserId(any()) }
    }

    @Test
    fun `should throw exception when userId format is invalid`() {
        // Given
        val invalidUserId = "invalid-uuid-format"

        // When & Then
        assertThrows<IllegalArgumentException> {
            getUserRatingsUseCase.execute(invalidUserId)
        }
    }

    @Test
    fun `should return single rating when user has one rating`() {
        // Given
        val userId = UserId.generate()
        val userIdString = userId.toString()
        val movieId = MovieId.generate()

        val user = User.fromPersistence(
            id = userId,
            email = Email("jane.smith@example.com"),
            username = "janesmith",
            fullName = "Jane Smith",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val rating = Rating.fromPersistence(
            userId = userId,
            movieId = movieId,
            value = RatingValue.of(3),
            comment = null,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        every { userRepository.findById(userId) } returns user
        every { ratingRepository.findByUserId(userId) } returns listOf(rating)

        // When
        val result = getUserRatingsUseCase.execute(userIdString)

        // Then
        assertEquals(1, result.size)
        assertEquals(userIdString, result[0].userId)
        assertEquals(movieId.toString(), result[0].movieId)
        assertEquals(3, result[0].value)
        assertEquals(null, result[0].comment)

        verify { userRepository.findById(userId) }
        verify { ratingRepository.findByUserId(userId) }
    }

    @Test
    fun `should return ratings with updated timestamps when available`() {
        // Given
        val userId = UserId.generate()
        val userIdString = userId.toString()
        val movieId = MovieId.generate()

        val user = User.fromPersistence(
            id = userId,
            email = Email("bob.wilson@example.com"),
            username = "bobwilson",
            fullName = "Bob Wilson",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val rating = Rating.fromPersistence(
            userId = userId,
            movieId = movieId,
            value = RatingValue.of(4),
            comment = "Updated rating",
            createdAt = LocalDateTime.now().minusDays(3),
            updatedAt = LocalDateTime.now().minusDays(1)
        )

        every { userRepository.findById(userId) } returns user
        every { ratingRepository.findByUserId(userId) } returns listOf(rating)

        // When
        val result = getUserRatingsUseCase.execute(userIdString)

        // Then
        assertEquals(1, result.size)
        assertEquals(rating.createdAt, result[0].createdAt)
        assertEquals(rating.updatedAt, result[0].updatedAt)

        verify { userRepository.findById(userId) }
        verify { ratingRepository.findByUserId(userId) }
    }
}
