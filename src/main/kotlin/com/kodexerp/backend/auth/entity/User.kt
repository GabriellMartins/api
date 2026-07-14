package com.kodexerp.backend.auth.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document(collection = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    val name: String,

    @Indexed(unique = true)
    val email: String,

    val password: String?,

    val phone: String? = null,

    val company: String? = null,

    val role: UserRole = UserRole.USER,

    val avatar: String? = null,

    @Indexed
    val isActive: Boolean = true,

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    val lastLogin: LocalDateTime? = null
)

enum class UserRole {
    USER,
    ADMIN
}
