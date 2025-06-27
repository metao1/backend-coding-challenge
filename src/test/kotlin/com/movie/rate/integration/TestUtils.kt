package com.movie.rate.integration

fun createTestUser(
    email: String = "user.test.${System.currentTimeMillis()}@example.com",
    username: String = "usertest${System.currentTimeMillis()}",
    fullName: String = "User Test ${System.currentTimeMillis()}"
): Map<String, Any> {
    return mapOf(
        "email" to email,
        "username" to username,
        "full_name" to fullName
    )
}

// Movie-specific test data initialization
fun createTestMovie(
    title: String = "Movie Test ${System.currentTimeMillis()}",
    description: String,
    releaseDate: String,
    genre: String,
    director: String
): Map<String, Any> {
    return mapOf(
        "title" to title,
        "description" to description,
        "release_date" to releaseDate,
        "genre" to genre,
        "director" to director
    )
}


