package com.movie.rate.domain.repositories

import com.movie.rate.domain.entities.Rating
import com.movie.rate.domain.valueobjects.MovieId
import com.movie.rate.domain.valueobjects.UserId

interface RatingRepository {
    fun save(rating: Rating): Rating

    fun findByUserIdAndMovieId(
        userId: UserId,
        movieId: MovieId,
    ): Rating?

    fun findByUserId(userId: UserId): List<Rating>
}
