package com.kodexerp.backend.admin.dto.response

import com.kodexerp.backend.auth.entity.UserRole
import java.time.LocalDateTime
import java.util.*

data class UsersListResponse(
    val success: Boolean,
    val data: UsersListData
)

data class UsersListData(
    val users: List<UserListItem>,
    val pagination: PaginationData
)

data class UserListItem(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String?,
    val company: String?,
    val role: UserRole,
    val isActive: Boolean,
    val plan: String?,
    val createdAt: LocalDateTime,
    val lastLogin: LocalDateTime?
)

data class PaginationData(
    val page: Int,
    val limit: Int,
    val total: Long,
    val totalPages: Int
)
