package com.movie.rate.application.dto

data class CreateRatingRequest(
    val userId: String,
    val movieId: String,
    val value: Int,
    val comment: String?
)
