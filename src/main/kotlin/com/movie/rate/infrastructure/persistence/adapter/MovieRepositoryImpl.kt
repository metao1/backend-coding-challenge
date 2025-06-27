package com.movie.rate.infrastructure.persistence.adapter

import com.movie.rate.domain.entities.Movie
import com.movie.rate.domain.repositories.MovieRepository
import com.movie.rate.domain.repositories.PageRequest
import com.movie.rate.domain.repositories.PageResult
import com.movie.rate.domain.repositories.SortDirection
import com.movie.rate.domain.valueobjects.MovieId
import com.movie.rate.infrastructure.entities.MovieJpaEntity
import com.movie.rate.infrastructure.persistence.repositories.MovieJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.data.domain.PageRequest as SpringPageRequest

@Repository
class MovieRepositoryImpl(
    private val movieJpaRepository: MovieJpaRepository
) : MovieRepository {

    override fun save(movie: Movie): Movie {
        val jpaEntity = MovieJpaEntity.fromDomain(movie)
        val savedEntity = movieJpaRepository.save(jpaEntity)
        return savedEntity.toDomain()
    }

    override fun findById(id: MovieId): Movie? {
        return movieJpaRepository.findByUuid(id.value)?.toDomain()
    }

    override fun findAll(pageRequest: PageRequest): PageResult<Movie> {
        val springPageRequest = createSpringPageRequest(pageRequest)
        val page = movieJpaRepository.findAll(springPageRequest)
        return createPageResult(page, pageRequest)
    }

    override fun existsByTitle(title: String): Boolean {
        return movieJpaRepository.existsByTitle(title)
    }

    private fun createSpringPageRequest(pageRequest: PageRequest): SpringPageRequest {
        val sort = when (pageRequest.sortDirection) {
            SortDirection.ASC -> Sort.by(pageRequest.sortBy).ascending()
            SortDirection.DESC -> Sort.by(pageRequest.sortBy).descending()
        }
        return SpringPageRequest.of(pageRequest.page, pageRequest.size, sort)
    }

    private fun createPageResult(
        page: Page<MovieJpaEntity>,
        pageRequest: PageRequest
    ): PageResult<Movie> {
        return PageResult(
            content = page.content.map { it.toDomain() },
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext(),
            hasPrevious = page.hasPrevious()
        )
    }

}
