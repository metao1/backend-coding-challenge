package com.movie.rate.application.usecases

import com.movie.rate.application.dto.CreateMovieRequest
import com.movie.rate.application.dto.MovieResponse
import com.movie.rate.domain.entities.Movie
import com.movie.rate.domain.repositories.MovieRepository
import com.movie.rate.domain.valueobjects.MovieId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateMovieUseCase(
    private val movieRepository: MovieRepository,
) {
    fun execute(request: CreateMovieRequest): MovieResponse {
        // Create and save movie
        val movie =
            Movie.create(
                id = MovieId.generate(),
                title = request.title,
                description = request.description,
                releaseDate = request.releaseDate,
                genre = request.genre,
                director = request.director,
            )

        val savedMovie = movieRepository.save(movie)
        return MovieResponse.fromDomain(savedMovie)
    }
}
