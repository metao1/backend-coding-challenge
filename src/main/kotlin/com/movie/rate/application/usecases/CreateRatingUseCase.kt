package com.movie.rate.application.usecases

import com.movie.rate.application.dto.CreateRatingRequest
import com.movie.rate.application.dto.RatingResponse
import com.movie.rate.domain.entities.Rating
import com.movie.rate.domain.exception.ConcurrencyException
import com.movie.rate.domain.exception.DuplicateRatingException
import com.movie.rate.domain.exception.MovieNotFoundException
import com.movie.rate.domain.exception.UserNotFoundException
import com.movie.rate.domain.repositories.MovieRepository
import com.movie.rate.domain.repositories.RatingRepository
import com.movie.rate.domain.repositories.UserRepository
import com.movie.rate.domain.valueobjects.MovieId
import com.movie.rate.domain.valueobjects.RatingValue
import com.movie.rate.domain.valueobjects.UserId
import jakarta.persistence.OptimisticLockException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateRatingUseCase(
    private val ratingRepository: RatingRepository,
    private val userRepository: UserRepository,
    private val movieRepository: MovieRepository,
) {
    @Retryable(
        value = [OptimisticLockException::class, ConcurrencyException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 100, multiplier = 2.0),
    )
    fun execute(request: CreateRatingRequest): RatingResponse {
        val userId = UserId.fromString(request.userId)
        val movieId = MovieId.fromString(request.movieId)

        // Verify user exists
        userRepository.findById(userId) ?: throw UserNotFoundException(request.userId)

        // Verify movie exists
        movieRepository.findById(movieId) ?: throw MovieNotFoundException(request.movieId)

        // Handle rating creation/update with concurrency protection
        return try {
            val existingRating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
            if (existingRating != null) {
                // Update existing rating (naturally idempotent)
                existingRating.updateRating(RatingValue.of(request.value), request.comment)
                val updatedRating = ratingRepository.save(existingRating)
                RatingResponse.fromDomain(updatedRating)
            } else {
                // Create new rating
                val rating =
                    Rating.create(
                        userId = userId,
                        movieId = movieId,
                        value = RatingValue.of(request.value),
                        comment = request.comment,
                    )

                val savedRating = ratingRepository.save(rating)
                RatingResponse.fromDomain(savedRating)
            }
        } catch (ex: DataIntegrityViolationException) {
            // Handle race condition: another thread created the rating
            // Fetch and update it (maintains idempotency)
            val existingRating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
                ?: throw DuplicateRatingException(request.userId, request.movieId, ex)

            existingRating.updateRating(RatingValue.of(request.value), request.comment)
            val updatedRating = ratingRepository.save(existingRating)
            RatingResponse.fromDomain(updatedRating)
        } catch (ex: OptimisticLockException) {
            // Handle optimistic locking failure
            throw ConcurrencyException("Rating was modified by another process. Please retry.", ex)
        }
    }
}
