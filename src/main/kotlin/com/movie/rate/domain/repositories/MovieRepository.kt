package com.movie.rate.domain.repositories

import com.movie.rate.domain.entities.Movie
import com.movie.rate.domain.valueobjects.MovieId

data class PageRequest(
    val page: Int = 0,
    val size: Int = 20,
    val sortBy: String = "createdAt",
    val sortDirection: SortDirection = SortDirection.DESC,
) {
    init {
        require(page >= 0) { "Page number must be non-negative" }
        require(size > 0 && size <= 100) { "Page size must be between 1 and 100" }
    }
}

enum class SortDirection {
    ASC,
    DESC,
}

data class PageResult<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
)

interface MovieRepository {
    fun save(movie: Movie): Movie

    fun findById(id: MovieId): Movie?

    fun findAll(pageRequest: PageRequest): PageResult<Movie>

    fun existsByTitle(title: String): Boolean
}
