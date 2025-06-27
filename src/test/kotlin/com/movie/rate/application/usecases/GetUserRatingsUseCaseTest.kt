package com.movie.rate.application.usecases

import com.movie.rate.application.domain.valueobjects.Email
import com.movie.rate.application.domain.valueobjects.MovieId
import com.movie.rate.application.domain.valueobjects.RatingValue
import com.movie.rate.application.domain.valueobjects.UserId
import com.movie.rate.domain.entities.Rating
import com.movie.rate.domain.entities.User
import com.movie.rate.domain.repositories.RatingRepository
import com.movie.rate.domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
}
