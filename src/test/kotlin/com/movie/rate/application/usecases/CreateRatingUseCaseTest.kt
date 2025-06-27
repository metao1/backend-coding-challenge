package com.movie.rate.application.usecases

import com.movie.rate.application.domain.valueobjects.Email
import com.movie.rate.application.domain.valueobjects.MovieId
import com.movie.rate.application.domain.valueobjects.RatingValue
import com.movie.rate.application.domain.valueobjects.UserId
import com.movie.rate.application.dto.CreateRatingRequest
import com.movie.rate.domain.entities.Movie
import com.movie.rate.domain.entities.Rating
import com.movie.rate.domain.entities.User
import com.movie.rate.domain.exception.MovieNotFoundException
import com.movie.rate.domain.exception.UserNotFoundException
import com.movie.rate.domain.repositories.MovieRepository
import com.movie.rate.domain.repositories.RatingRepository
import com.movie.rate.domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime

class CreateRatingUseCaseTest {

    private val ratingRepository = mockk<RatingRepository>()
    private val userRepository = mockk<UserRepository>()
    private val movieRepository = mockk<MovieRepository>()
    private val createRatingUseCase = CreateRatingUseCase(ratingRepository, userRepository, movieRepository)

    @Test
    fun `should create new rating successfully when user and movie exist`() {
        // Given
        val userId = UserId.generate()
        val movieId = MovieId.generate()
        val request = CreateRatingRequest(
            userId = userId.toString(),
            movieId = movieId.toString(),
            value = 5,
            comment = "Excellent movie!"
        )

        val user = User.fromPersistence(
            id = userId,
            email = Email("john.doe@example.com"),
            username = "johndoe",
            fullName = "John Doe",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val movie = Movie.fromPersistence(
            id = movieId,
            title = "The Matrix",
            description = "A computer hacker learns about reality.",
            releaseDate = LocalDate.of(1999, 3, 31),
            genre = "Science Fiction",
            director = "The Wachowskis",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        every { userRepository.findById(userId) } returns user
        every { movieRepository.findById(movieId) } returns movie
        every { ratingRepository.findByUserIdAndMovieId(userId, movieId) } returns null
        every { ratingRepository.save(any()) } answers { firstArg() }

        // When
        val result = createRatingUseCase.execute(request)

        // Then
        assertEquals(userId.toString(), result.userId)
        assertEquals(movieId.toString(), result.movieId)
        assertEquals(5, result.value)
        assertEquals("Excellent movie!", result.comment)
        assertNotNull(result.createdAt)

        verify { userRepository.findById(userId) }
        verify { movieRepository.findById(movieId) }
        verify { ratingRepository.findByUserIdAndMovieId(userId, movieId) }
        verify { ratingRepository.save(any()) }
    }

    @Test
    fun `should update existing rating when rating already exists`() {
        // Given
        val userId = UserId.generate()
        val movieId = MovieId.generate()
        val request = CreateRatingRequest(
            userId = userId.toString(),
            movieId = movieId.toString(),
            value = 4,
            comment = "Updated comment"
        )

        val user = User.fromPersistence(
            id = userId,
            email = Email("john.doe@example.com"),
            username = "johndoe",
            fullName = "John Doe",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val movie = Movie.fromPersistence(
            id = movieId,
            title = "The Matrix",
            description = "A computer hacker learns about reality.",
            releaseDate = LocalDate.of(1999, 3, 31),
            genre = "Science Fiction",
            director = "The Wachowskis",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val existingRating = Rating.fromPersistence(
            userId = userId,
            movieId = movieId,
            value = RatingValue.of(3),
            comment = "Original comment",
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = null
        )

        every { userRepository.findById(userId) } returns user
        every { movieRepository.findById(movieId) } returns movie
        every { ratingRepository.findByUserIdAndMovieId(userId, movieId) } returns existingRating
        every { ratingRepository.save(any()) } answers { firstArg() }

        // When
        val result = createRatingUseCase.execute(request)

        // Then
        assertEquals(userId.toString(), result.userId)
        assertEquals(movieId.toString(), result.movieId)
        assertEquals(4, result.value)
        assertEquals("Updated comment", result.comment)
        assertNotNull(result.updatedAt)

        verify { userRepository.findById(userId) }
        verify { movieRepository.findById(movieId) }
        verify { ratingRepository.findByUserIdAndMovieId(userId, movieId) }
        verify { ratingRepository.save(any()) }
    }

    @Test
    fun `should throw exception when user does not exist`() {
        // Given
        val userId = UserId.generate()
        val movieId = MovieId.generate()
        val request = CreateRatingRequest(
            userId = userId.toString(),
            movieId = movieId.toString(),
            value = 5,
            comment = "Great movie!"
        )

        every { userRepository.findById(userId) } returns null

        // When & Then
        val exception = assertThrows<UserNotFoundException> {
            createRatingUseCase.execute(request)
        }

        assertEquals("User with identifier '${userId}' not found", exception.message)
        verify { userRepository.findById(userId) }
        verify(exactly = 0) { movieRepository.findById(any()) }
        verify(exactly = 0) { ratingRepository.save(any()) }
    }

    @Test
    fun `should throw exception when movie does not exist`() {
        // Given
        val userId = UserId.generate()
        val movieId = MovieId.generate()
        val request = CreateRatingRequest(
            userId = userId.toString(),
            movieId = movieId.toString(),
            value = 5,
            comment = "Great movie!"
        )

        val user = User.fromPersistence(
            id = userId,
            email = Email("john.doe@example.com"),
            username = "johndoe",
            fullName = "John Doe",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        every { userRepository.findById(userId) } returns user
        every { movieRepository.findById(movieId) } returns null

        // When & Then
        val exception = assertThrows<MovieNotFoundException> {
            createRatingUseCase.execute(request)
        }

        assertEquals("Movie with identifier '${movieId}' not found", exception.message)
        verify { userRepository.findById(userId) }
        verify { movieRepository.findById(movieId) }
        verify(exactly = 0) { ratingRepository.save(any()) }
    }

    @Test
    fun `should create rating without comment`() {
        // Given
        val userId = UserId.generate()
        val movieId = MovieId.generate()
        val request = CreateRatingRequest(
            userId = userId.toString(),
            movieId = movieId.toString(),
            value = 3,
            comment = null
        )

        val user = User.fromPersistence(
            id = userId,
            email = Email("jane.doe@example.com"),
            username = "janedoe",
            fullName = "Jane Doe",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val movie = Movie.fromPersistence(
            id = movieId,
            title = "Inception",
            description = "A thief who steals corporate secrets through dreams.",
            releaseDate = LocalDate.of(2010, 7, 16),
            genre = "Action",
            director = "Christopher Nolan",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        every { userRepository.findById(userId) } returns user
        every { movieRepository.findById(movieId) } returns movie
        every { ratingRepository.findByUserIdAndMovieId(userId, movieId) } returns null
        every { ratingRepository.save(any()) } answers { firstArg() }

        // When
        val result = createRatingUseCase.execute(request)

        // Then
        assertEquals(userId.toString(), result.userId)
        assertEquals(movieId.toString(), result.movieId)
        assertEquals(3, result.value)
        assertEquals(null, result.comment)

        verify { ratingRepository.save(any()) }
    }
}
