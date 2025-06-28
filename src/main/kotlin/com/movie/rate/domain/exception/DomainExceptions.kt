package com.movie.rate.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Base class for resource not found exceptions.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
abstract class ResourceNotFoundException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

class MovieNotFoundException(
    movieId: String,
    cause: Throwable? = null,
) : ResourceNotFoundException("Movie with identifier '$movieId' not found", cause)

class UserAlreadyExistsException(
    identifier: String,
    cause: Throwable? = null,
) : ResourceNotFoundException("User with identifier '$identifier' already exists", cause)

class UserNotFoundException(
    identifier: String,
    cause: Throwable? = null,
) : ResourceNotFoundException("User with identifier '$identifier' not found", cause)

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
