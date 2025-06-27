package com.movie.rate.domain.entities

import com.movie.rate.domain.valueobjects.MovieId
import com.movie.rate.domain.valueobjects.RatingValue
import com.movie.rate.domain.valueobjects.UserId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class RatingTest {

    @Test
    fun `should create rating with valid data`() {
        // Given
        val userId = UserId.generate()
        val movieId = MovieId.generate()
        val ratingValue = RatingValue.of(4)
        val comment = "Great movie with excellent special effects!"

        // When
        val rating = Rating.create(userId, movieId, ratingValue, comment)

        // Then
        assertEquals(userId, rating.userId)
        assertEquals(movieId, rating.movieId)
        assertEquals(ratingValue, rating.value)
        assertEquals(comment, rating.comment)
        assertNotNull(rating.createdAt)
    }

    @Test
    fun `should create rating without comment`() {
        // Given
        val userId = UserId.generate()
        val movieId = MovieId.generate()
        val ratingValue = RatingValue.of(5)

        // When
        val rating = Rating.create(userId, movieId, ratingValue, null)

        // Then
        assertEquals(userId, rating.userId)
        assertEquals(movieId, rating.movieId)
        assertEquals(ratingValue, rating.value)
        assertEquals(null, rating.comment)
        assertNotNull(rating.createdAt)
    }

    @Test
    fun `should update rating value and comment`() {
        // Given
        val rating = Rating.create(
            UserId.generate(),
            MovieId.generate(),
            RatingValue.of(3),
            "It was okay"
        )
        val newRatingValue = RatingValue.of(5)
        val newComment = "Actually, it was amazing on second viewing!"

        // When
        rating.updateRating(newRatingValue, newComment)

        // Then
        assertEquals(newRatingValue, rating.value)
        assertEquals(newComment, rating.comment)
        assertNotNull(rating.updatedAt)
    }

    @Test
    fun `should update rating value without comment`() {
        // Given
        val rating = Rating.create(
            UserId.generate(),
            MovieId.generate(),
            RatingValue.of(3),
            "It was okay"
        )
        val newRatingValue = RatingValue.of(4)

        // When
        rating.updateRating(newRatingValue, null)

        // Then
        assertEquals(newRatingValue, rating.value)
        assertEquals(null, rating.comment)
        assertNotNull(rating.updatedAt)
    }
}
