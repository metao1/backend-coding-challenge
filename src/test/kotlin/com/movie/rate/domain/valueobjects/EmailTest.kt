package com.movie.rate.domain.valueobjects

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for Email value object.
 * Tests email validation and boundary conditions.
 */
class EmailTest {

    @Test
    fun `should create email with valid format`() {
        val validEmails = listOf(
            "user@example.com",
            "test.email@domain.org",
            "user+tag@example.co.uk",
            "firstname.lastname@company.com",
            "user123@test-domain.com"
        )

        validEmails.forEach { emailString ->
            val email = Email(emailString)
            assertEquals(emailString, email.value)
        }
    }

    @Test
    fun `should throw exception for invalid email format`() {
        val invalidEmails = listOf(
            "",                    // Empty string
            "   ",                 // Blank string
            "invalid",             // No @ symbol
            "@domain.com",         // Missing local part
            "user@",               // Missing domain
            "user@domain",         // Missing TLD
            "user name@domain.com", // Space in local part
            "user@domain .com",    // Space in domain
            "user@@domain.com",    // Double @
            "user@domain@com"      // Multiple @
        )

        invalidEmails.forEach { invalidEmail ->
            assertThrows<IllegalStateException>("Should fail for: $invalidEmail") {
                Email(invalidEmail)
            }
        }
    }

    @Test
    fun `should throw exception for null email`() {
        assertThrows<IllegalStateException> {
            Email("")
        }
    }

    @Test
    fun `should throw exception for blank email`() {
        assertThrows<IllegalStateException> {
            Email("   ")
        }
    }

    @Test
    fun `should handle case sensitivity correctly`() {
        val email1 = Email("User@Example.COM")
        val email2 = Email("user@example.com")

        // Email should preserve original case
        assertEquals("User@Example.COM", email1.value)
        assertEquals("user@example.com", email2.value)
    }

    @Test
    fun `should have correct string representation`() {
        val email = Email("test@example.com")
        assertEquals("test@example.com", email.toString())
    }

    @Test
    fun `should handle international domain names`() {
        val validInternationalEmails = listOf(
            "user@example.co.uk",
            "test@domain.org",
            "email@sub.domain.com"
        )

        validInternationalEmails.forEach { emailString ->
            val email = Email(emailString)
            assertEquals(emailString, email.value)
        }
    }

    @Test
    fun `should reject emails with invalid characters`() {
        val invalidCharacterEmails = listOf(
            "user<>@domain.com",
            "user[]@domain.com",
            "user{}@domain.com",
            "user()@domain.com",
            "user;@domain.com",
            "user:@domain.com",
            "user,@domain.com",
            "user@domain@com"
        )

        invalidCharacterEmails.forEach { invalidEmail ->
            assertThrows<IllegalStateException>("Should fail for: $invalidEmail") {
                Email(invalidEmail)
            }
        }
    }
}
