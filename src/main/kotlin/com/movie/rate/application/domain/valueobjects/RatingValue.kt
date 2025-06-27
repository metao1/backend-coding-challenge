package com.movie.rate.application.domain.valueobjects

@JvmInline
value class RatingValue(val value: Int) {

    init {
        check(value in MIN_RATING..MAX_RATING) {
            "Rating value must be between $MIN_RATING and $MAX_RATING but was $value"
        }
    }

    companion object {
        const val MIN_RATING = 1
        const val MAX_RATING = 5

        fun of(value: Int): RatingValue = RatingValue(value)
    }

    override fun toString(): String = value.toString()
}
