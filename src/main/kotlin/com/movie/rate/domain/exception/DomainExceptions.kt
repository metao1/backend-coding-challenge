package com.movie.rate.domain.exception

class DomainExceptions {

    class MovieNotFoundException(
        movieId: String,
        cause: Throwable? = null
    ) : RuntimeException("Movie with identifier '$movieId' not found", cause)

    class UserAlreadyExistsException(
        identifier: String,
        cause: Throwable? = null
    ) : RuntimeException("User with identifier '$identifier' already exists", cause)
}
