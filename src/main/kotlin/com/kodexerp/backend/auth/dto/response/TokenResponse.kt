package com.kodexerp.backend.auth.dto.response

data class TokenResponse(
    val success: Boolean,
    val data: TokenData
)

data class TokenData(
    val token: String,
    val refreshToken: String
)
