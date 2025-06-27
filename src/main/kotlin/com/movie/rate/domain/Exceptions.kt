package com.movie.rate.domain

class Exceptions {

    class MovieNotFoundException(
        movieId: String,
        cause: Throwable? = null
    ) : RuntimeException("Movie with identifier '$movieId' not found", cause)
}
