package com.movie.rate.presentation.dto

import com.movie.rate.domain.repositories.PageRequest
import com.movie.rate.domain.repositories.SortDirection
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

/**
 * DTO for pagination parameters in API requests.
 * Provides validation and conversion to domain PageRequest.
 */
data class PaginationRequestDto(
    @field:Min(value = 0, message = "Page number must be 0 or greater")
    val page: Int = 0,

    @field:Min(value = 1, message = "Page size must be at least 1")
    @field:Max(value = 100, message = "Page size must not exceed 100")
    val size: Int = 20,

    val sortBy: String = "createdAt",
    val sortDirection: String = "DESC"
) {
    fun toDomainPageRequest(): PageRequest {
        return PageRequest(
            page = page,
            size = size,
            sortBy = sortBy,
            sortDirection = SortDirection.valueOf(sortDirection.uppercase())
        )
    }
}
