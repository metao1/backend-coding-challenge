package com.movie.rate.application.usecases

import com.movie.rate.domain.valueobjects.Email
import com.movie.rate.application.dto.CreateUserRequestDto
import com.movie.rate.domain.exception.UserAlreadyExistsException
import com.movie.rate.domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateUserUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val createUserUseCase = CreateUserUseCase(userRepository)

    @Test
    fun `should create user successfully`() {
        // Given
        val request = CreateUserRequestDto(
            email = "john.doe@example.com",
            username = "johndoe",
            fullName = "John Doe"
        )

        every { userRepository.existsByEmail(Email(request.email)) } returns false
        every { userRepository.existsByUsername(request.username) } returns false
        every { userRepository.save(any()) } answers { firstArg() }

        // When
        val result = createUserUseCase.execute(request)

        // Then
        assertNotNull(result.id)
        assertEquals(request.email, result.email)
        assertEquals(request.username, result.username)
        assertEquals(request.fullName, result.fullName)

        verify { userRepository.existsByEmail(Email(request.email)) }
        verify { userRepository.existsByUsername(request.username) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `should throw exception when email already exists`() {
        // Given
        val request = CreateUserRequestDto(
            email = "john.doe@example.com",
            username = "johndoe",
            fullName = "John Doe"
        )

        every { userRepository.existsByEmail(Email(request.email)) } returns true

        // When & Then
        assertThrows<UserAlreadyExistsException> {
            createUserUseCase.execute(request)
        }

        verify { userRepository.existsByEmail(Email(request.email)) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should throw exception when username already exists`() {
        // Given
        val request = CreateUserRequestDto(
            email = "john.doe@example.com",
            username = "johndoe",
            fullName = "John Doe"
        )

        every { userRepository.existsByEmail(Email(request.email)) } returns false
        every { userRepository.existsByUsername(request.username) } returns true

        // When & Then
        assertThrows<UserAlreadyExistsException> {
            createUserUseCase.execute(request)
        }

        verify { userRepository.existsByEmail(Email(request.email)) }
        verify { userRepository.existsByUsername(request.username) }
        verify(exactly = 0) { userRepository.save(any()) }
    }
}
