package com.movie.rate.domain.entities

import com.movie.rate.domain.valueobjects.Email
import com.movie.rate.domain.valueobjects.UserId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for User entity.
 * Tests user creation and behavior without validation (handled at API layer).
 */
class UserTest {
    @Test
    fun `should create user with valid data`() {
        // Given
        val userId = UserId.generate()
        val email = Email("john.doe@example.com")
        val username = "johndoe"
        val fullName = "John Doe"

        // When
        val user = User.create(userId, email, username, fullName)

        // Then
        assertEquals(userId, user.id)
        assertEquals(email, user.email)
        assertEquals(username, user.username)
        assertEquals(fullName, user.fullName)
        assertNotNull(user.createdAt)
        assertNull(user.updatedAt)
    }

    @Test
    fun `should have correct equals and hashCode based on id`() {
        // Given
        val userId = UserId.generate()
        val email1 = Email("john@example.com")
        val email2 = Email("jane@example.com")

        val user1 = User.create(userId, email1, "john", "John Doe")
        val user2 = User.create(userId, email2, "jane", "Jane Doe")
        val user3 = User.create(UserId.generate(), email1, "john", "John Doe")

        // Then
        assertEquals(user1, user2) // Same ID
        assertNotEquals(user1, user3) // Different ID
        assertEquals(user1.hashCode(), user2.hashCode()) // Same ID
        assertNotEquals(user1.hashCode(), user3.hashCode()) // Different ID
    }

    @Test
    fun `should have meaningful toString`() {
        // Given
        val userId = UserId.generate()
        val email = Email("john.doe@example.com")
        val user = User.create(userId, email, "johndoe", "John Doe")

        // When
        val toString = user.toString()

        // Then
        assertTrue(toString.contains("johndoe"))
        assertTrue(toString.contains("john.doe@example.com"))
        assertTrue(toString.contains("John Doe"))
        assertTrue(toString.contains(userId.toString()))
    }
}
