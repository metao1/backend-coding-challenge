package com.movie.rate.application.usecases

import com.movie.rate.application.dto.CreateRatingRequest
import com.movie.rate.application.dto.RatingResponse
import com.movie.rate.domain.entities.Rating
import com.movie.rate.domain.exception.MovieNotFoundException
import com.movie.rate.domain.exception.UserNotFoundException
import com.movie.rate.domain.repositories.MovieRepository
import com.movie.rate.domain.repositories.RatingRepository
import com.movie.rate.domain.repositories.UserRepository
import com.movie.rate.domain.valueobjects.MovieId
import com.movie.rate.domain.valueobjects.RatingValue
import com.movie.rate.domain.valueobjects.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateRatingUseCase(
    private val ratingRepository: RatingRepository,
    private val userRepository: UserRepository,
    private val movieRepository: MovieRepository,
) {
    fun execute(request: CreateRatingRequest): RatingResponse {
        val userId = UserId.fromString(request.userId)
        val movieId = MovieId.fromString(request.movieId)

        // Verify user exists
        userRepository.findById(userId) ?: throw UserNotFoundException(request.userId)

        // Verify movie exists
        movieRepository.findById(movieId) ?: throw MovieNotFoundException(request.movieId)

        // Check if rating already exists and handle appropriately
        val existingRating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
        return if (existingRating != null) {
            // Update existing rating
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
    }
}
