package com.kodexerp.backend.shared.dto.response

import com.kodexerp.backend.auth.entity.UserRole
import java.time.LocalDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String?,
    val company: String?,
    val role: UserRole,
    val avatar: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
