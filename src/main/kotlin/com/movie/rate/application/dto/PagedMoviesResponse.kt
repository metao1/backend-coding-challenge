package com.movie.rate.application.dto

data class PagedMoviesResponse(
    val movies: List<MovieResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
)
