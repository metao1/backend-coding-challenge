package com.movie.rate.infrastructure.persistence

import com.movie.rate.application.domain.valueobjects.MovieId
import com.movie.rate.domain.entities.Movie
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.NaturalId
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "movies")
class MovieJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @NaturalId
    @Column(name = "uuid", unique = true, nullable = false)
    val uuid: UUID,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "description", nullable = false, length = 2000)
    var description: String,

    @Column(name = "release_date", nullable = false)
    val releaseDate: LocalDate,

    @Column(name = "genre", nullable = false)
    val genre: String,

    @Column(name = "director", nullable = false)
    val director: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime?
) {
    companion object {
        fun fromDomain(movie: Movie): MovieJpaEntity = MovieJpaEntity(
            uuid = movie.id.value,
            title = movie.title,
            description = movie.description,
            releaseDate = movie.releaseDate,
            genre = movie.genre,
            director = movie.director,
            createdAt = movie.createdAt,
            updatedAt = movie.updatedAt
        )
    }

    fun toDomain(): Movie = Movie.fromPersistence(
        id = MovieId(uuid),
        title = title,
        description = description,
        releaseDate = releaseDate,
        genre = genre,
        director = director,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    // Best practices for JPA entities - use natural identifier (UUID)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MovieJpaEntity) return false
        return uuid == other.uuid
    }

    override fun hashCode(): Int = uuid.hashCode()

    override fun toString(): String {
        return "MovieJpaEntity(id=$id, uuid=$uuid, title='$title', genre='$genre', director='$director', releaseDate=$releaseDate)"
    }
}
