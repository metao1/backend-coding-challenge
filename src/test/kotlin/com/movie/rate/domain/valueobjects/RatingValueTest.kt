package com.movie.rate.domain.valueobjects

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/**
 * Unit tests for RatingValue value object.
 * Tests rating value validation and boundary conditions.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RatingValueTest {
    @Test
    fun `should create rating value with valid values`() {
        // Test all valid rating values (1-5)
        for (value in 1..5) {
            val ratingValue = RatingValue.of(value)
            assertEquals(value, ratingValue.value)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "MIN_RATING, 1",
        "MAX_RATING, 5",
    )
    fun `should have correct constants`(
        constant: String,
        expected: Int,
    ) {
        val actual =
            when (constant) {
                "MIN_RATING" -> RatingValue.MIN_RATING
                "MAX_RATING" -> RatingValue.MAX_RATING
                else -> throw IllegalArgumentException("Unknown constant")
            }
        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @CsvSource(
        "0, Rating value must be between 1 and 5 but was 0",
        "6, Rating value must be between 1 and 5 but was 6",
        "-1, Rating value must be between 1 and 5 but was -1",
        "100, Rating value must be between 1 and 5 but was 100",
    )
    fun `should throw exception for invalid rating values`(
        invalidValue: Int,
        expectedMessage: String,
    ) {
        val exception =
            assertThrows<IllegalStateException> {
                RatingValue.of(invalidValue)
            }
        assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun `should have correct string representation`() {
        val ratingValue = RatingValue.of(4)
        assertEquals("4", ratingValue.toString())
    }
}
