package com.movie.rate.application.usecases

import com.movie.rate.domain.valueobjects.UserId
import com.movie.rate.application.dto.RatingResponse
import com.movie.rate.domain.exception.UserNotFoundException
import com.movie.rate.domain.repositories.RatingRepository
import com.movie.rate.domain.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetUserRatingsUseCase(
    private val ratingRepository: RatingRepository,
    private val userRepository: UserRepository
) {
    fun execute(userId: String): List<RatingResponse> {
        val id = UserId.fromString(userId)

        // Verify user exists
        userRepository.findById(id)
            ?: throw UserNotFoundException(userId)

        return ratingRepository.findByUserId(id).map { RatingResponse.fromDomain(it) }
    }
}
