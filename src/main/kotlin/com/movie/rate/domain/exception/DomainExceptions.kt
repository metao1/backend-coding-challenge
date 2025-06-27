package com.movie.rate.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class MovieNotFoundException(
    movieId: String,
    cause: Throwable? = null
) : RuntimeException("Movie with identifier '$movieId' not found", cause)

@ResponseStatus(HttpStatus.CONFLICT)
class UserAlreadyExistsException(
    identifier: String,
    cause: Throwable? = null
) : RuntimeException("User with identifier '$identifier' already exists", cause)

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException(
    identifier: String,
    cause: Throwable? = null
) : RuntimeException("User with identifier '$identifier' not found", cause)
