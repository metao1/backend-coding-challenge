package com.movie.rate.domain.exceptions

/**
 * Exception thrown when a concurrency conflict occurs during data modification.
 * This typically happens when multiple instances try to modify the same entity simultaneously.
 */
class ConcurrencyException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Exception thrown when a duplicate rating is attempted to be created.
 * This happens when the unique constraint (user_uuid, movie_uuid) is violated.
 */
class DuplicateRatingException(
    userId: String,
    movieId: String,
    cause: Throwable? = null,
) : RuntimeException("Rating already exists for user $userId and movie $movieId", cause)
