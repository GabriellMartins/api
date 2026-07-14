package com.kodexerp.backend.auth.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document(collection = "refresh_tokens")
data class RefreshToken(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Indexed
    val userId: UUID,

    @Indexed(unique = true)
    val token: String,

    val expiresAt: LocalDateTime,

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    val isRevoked: Boolean = false
)
