package com.movie.rate.application.usecases

import com.movie.rate.application.domain.valueobjects.Email
import com.movie.rate.application.domain.valueobjects.UserId
import com.movie.rate.application.dto.CreateUserRequestDto
import com.movie.rate.application.dto.UserResponse
import com.movie.rate.domain.entities.User
import com.movie.rate.domain.exception.UserAlreadyExistsException
import com.movie.rate.domain.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(request: CreateUserRequestDto): UserResponse {
        val email = Email(request.email)

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException("email ${request.email}")
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.username)) {
            throw UserAlreadyExistsException("username ${request.username}")
        }

        // Create and save user
        val user = User.create(
            id = UserId.generate(),
            email = email,
            username = request.username,
            fullName = request.fullName
        )

        val savedUser = userRepository.save(user)
        return UserResponse.fromDomain(savedUser)
    }
}
