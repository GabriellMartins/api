package com.kodexerp.backend.auth.dto.response

import com.kodexerp.backend.shared.dto.response.UserResponse

data class AuthResponse(
    val success: Boolean,
    val data: AuthData
)

data class AuthData(
    val user: UserResponse,
    val token: String,
    val refreshToken: String
)
