package com.movie.rate.application.usecases

import com.movie.rate.domain.entities.User
import com.movie.rate.domain.exception.UserNotFoundException
import com.movie.rate.domain.repositories.UserRepository
import com.movie.rate.domain.valueobjects.Email
import com.movie.rate.domain.valueobjects.UserId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class GetUserUseCaseTest {
    private val userRepository = mockk<UserRepository>()
    private val getUserUseCase = GetUserUseCase(userRepository)

    @Test
    fun `should get user successfully when user exists`() {
        // Given
        val userId = UserId.generate()
        val userIdString = userId.toString()
        val user =
            User.fromPersistence(
                id = userId,
                email = Email("john.doe@example.com"),
                username = "johndoe",
                fullName = "John Doe",
                createdAt = LocalDateTime.now(),
                updatedAt = null,
            )

        every { userRepository.findById(userId) } returns user

        // When
        val result = getUserUseCase.execute(userIdString)

        // Then
        assertEquals(userIdString, result.id)
        assertEquals("john.doe@example.com", result.email)
        assertEquals("johndoe", result.username)
        assertEquals("John Doe", result.fullName)
        assertNotNull(result.createdAt)

        verify { userRepository.findById(userId) }
    }

    @Test
    fun `should throw exception when user does not exist`() {
        // Given
        val userId = UserId.generate()
        val userIdString = userId.toString()

        every { userRepository.findById(userId) } returns null

        // When & Then
        val exception =
            assertThrows<UserNotFoundException> {
                getUserUseCase.execute(userIdString)
            }

        assertEquals("User with identifier '$userIdString' not found", exception.message)
        verify { userRepository.findById(userId) }
    }

    @Test
    fun `should throw exception when userId format is invalid`() {
        // Given
        val invalidUserId = "invalid-uuid-format"

        // When & Then
        assertThrows<IllegalArgumentException> {
            getUserUseCase.execute(invalidUserId)
        }
    }
}
