package com.movie.rate.application.usecases

import com.movie.rate.domain.valueobjects.UserId
import com.movie.rate.application.dto.UserResponse
import com.movie.rate.domain.exception.UserNotFoundException
import com.movie.rate.domain.repositories.UserRepository

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
@Transactional(readOnly = true)
class GetUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(userId: String): UserResponse {
        val id = UserId.fromString(userId)
        val user = Optional.ofNullable(userRepository.findById(id))
            .orElseThrow { UserNotFoundException(userId) }

        return UserResponse.fromDomain(user)
    }
}
