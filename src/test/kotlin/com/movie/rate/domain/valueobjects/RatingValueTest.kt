package com.movie.rate.domain.valueobjects

import com.movie.rate.application.domain.valueobjects.RatingValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals

/**
 * Unit tests for RatingValue value object.
 * Tests rating value validation and boundary conditions.
 */
class RatingValueTest {

    @Test
    fun `should create rating value with valid values`() {
        // Test all valid rating values (1-5)
        for (value in 1..5) {
            val ratingValue = RatingValue.of(value)
            assertEquals(value, ratingValue.value)
        }
    }

    @Test
    fun `should create minimum valid rating value`() {
        val ratingValue = RatingValue.of(RatingValue.MIN_RATING)
        assertEquals(1, ratingValue.value)
    }

    @Test
    fun `should create maximum valid rating value`() {
        val ratingValue = RatingValue.of(RatingValue.MAX_RATING)
        assertEquals(5, ratingValue.value)
    }

    @Test
    fun `should throw exception for rating value below minimum`() {
        val exception = assertThrows<IllegalStateException> {
            RatingValue.of(0)
        }
        assertEquals("Rating value must be between 1 and 5, but was 0", exception.message)
    }

    @Test
    fun `should throw exception for rating value above maximum`() {
        val exception = assertThrows<IllegalStateException> {
            RatingValue.of(6)
        }
        assertEquals("Rating value must be between 1 and 5, but was 6", exception.message)
    }

    @Test
    fun `should throw exception for negative rating value`() {
        val exception = assertThrows<IllegalStateException> {
            RatingValue.of(-1)
        }
        assertEquals("Rating value must be between 1 and 5, but was -1", exception.message)
    }

    @Test
    fun `should throw exception for extremely high rating value`() {
        val exception = assertThrows<IllegalStateException> {
            RatingValue.of(100)
        }
        assertEquals("Rating value must be between 1 and 5, but was 100", exception.message)
    }

    @Test
    fun `should have correct string representation`() {
        val ratingValue = RatingValue.of(4)
        assertEquals("4", ratingValue.toString())
    }

    @Test
    fun `should have correct constants`() {
        assertEquals(1, RatingValue.MIN_RATING)
        assertEquals(5, RatingValue.MAX_RATING)
    }
}
