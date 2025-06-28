package com.movie.rate.infrastructure.entities

import com.movie.rate.domain.entities.User
import com.movie.rate.domain.valueobjects.Email
import com.movie.rate.domain.valueobjects.UserId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.NaturalId
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,
    @NaturalId
    @Column(name = "uuid", unique = true, nullable = false)
    val uuid: UUID,
    @Column(name = "email", unique = true, nullable = false)
    val email: String,
    @Column(name = "username", unique = true, nullable = false)
    val username: String,
    @Column(name = "full_name", nullable = false)
    var fullName: String,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime?,
) {
    companion object {
        fun fromDomain(user: User): UserJpaEntity =
            UserJpaEntity(
                uuid = user.id.value,
                email = user.email.value,
                username = user.username,
                fullName = user.fullName,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
            )
    }

    fun toDomain(): User =
        User.fromPersistence(
            id = UserId(uuid),
            email = Email(email),
            username = username,
            fullName = fullName,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    // Best practices for JPA entities - use natural identifier (UUID)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserJpaEntity) return false
        return uuid == other.uuid
    }

    override fun hashCode(): Int = uuid.hashCode()

    override fun toString(): String = "UserJpaEntity(id=$id, uuid=$uuid, email='$email', username='$username', fullName='$fullName')"
}
