package com.movie.rate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MovieRatingSystemApplication

fun main(args: Array<String>) {
    runApplication<MovieRatingSystemApplication>(*args)
}
