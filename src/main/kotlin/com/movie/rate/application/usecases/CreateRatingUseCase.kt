package com.movie.rate.application.usecases

import com.movie.rate.application.domain.valueobjects.MovieId
import com.movie.rate.application.domain.valueobjects.RatingValue
import com.movie.rate.application.domain.valueobjects.UserId
import com.movie.rate.application.dto.CreateRatingRequest
import com.movie.rate.application.dto.RatingResponse
import com.movie.rate.domain.entities.Rating
import com.movie.rate.domain.exception.DomainExceptions.MovieNotFoundException
import com.movie.rate.domain.exception.DomainExceptions.UserNotFoundException
import com.movie.rate.domain.repositories.MovieRepository
import com.movie.rate.domain.repositories.RatingRepository
import com.movie.rate.domain.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class CreateRatingUseCase(
    private val ratingRepository: RatingRepository,
    private val userRepository: UserRepository,
    private val movieRepository: MovieRepository
) {
    fun execute(request: CreateRatingRequest): RatingResponse {
        val userId = UserId.fromString(request.userId)
        val movieId = MovieId.fromString(request.movieId)

        // Verify user exists using Optional
        Optional.ofNullable(userRepository.findById(userId))
            .orElseThrow { UserNotFoundException(request.userId) }

        // Verify movie exists using Optional
        Optional.ofNullable(movieRepository.findById(movieId))
            .orElseThrow { MovieNotFoundException(request.movieId) }

        // Check if rating already exists and handle with Optional
        return Optional.ofNullable(ratingRepository.findByUserIdAndMovieId(userId, movieId))
            .map { existingRating ->
                // Update existing rating
                existingRating.updateRating(RatingValue.of(request.value), request.comment)
                val updatedRating = ratingRepository.save(existingRating)
                RatingResponse.fromDomain(updatedRating)
            }.orElseGet {
                // Create new rating
                val rating = Rating.create(
                    userId = userId,
                    movieId = movieId,
                    value = RatingValue.of(request.value),
                    comment = request.comment
                )

                val savedRating = ratingRepository.save(rating)
                RatingResponse.fromDomain(savedRating)
            }
    }
}
