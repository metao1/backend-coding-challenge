package com.movie.rate.application.usecases

import com.movie.rate.application.dto.MovieResponse
import com.movie.rate.application.dto.PagedMoviesResponse
import com.movie.rate.domain.repositories.MovieRepository
import com.movie.rate.domain.repositories.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetAllMoviesUseCase(
    private val movieRepository: MovieRepository,
) {
    fun execute(pageRequest: PageRequest): PagedMoviesResponse {
        val pageResult = movieRepository.findAll(pageRequest)
        val movies = pageResult.content.map { MovieResponse.fromDomain(it) }

        return PagedMoviesResponse(
            movies = movies,
            page = pageResult.page,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            hasNext = pageResult.hasNext,
            hasPrevious = pageResult.hasPrevious,
        )
    }
}
