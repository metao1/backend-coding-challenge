package com.movie.rate.application.usecases

import com.movie.rate.application.domain.valueobjects.MovieId
import com.movie.rate.application.dto.MovieResponse
import com.movie.rate.domain.exception.DomainExceptions
import com.movie.rate.domain.repositories.MovieRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class GetMovieUseCase(
    private val movieRepository: MovieRepository
) {
    fun execute(movieId: String): MovieResponse {
        val id = MovieId.fromString(movieId)
        val movie = Optional.ofNullable(movieRepository.findById(id))
            .orElseThrow { DomainExceptions.MovieNotFoundException(movieId) }

        return MovieResponse.fromDomain(movie)
    }
}
