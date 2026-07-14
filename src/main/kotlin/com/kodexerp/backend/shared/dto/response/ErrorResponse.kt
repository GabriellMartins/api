package com.kodexerp.backend.shared.dto.response

data class ErrorResponse(
    val success: Boolean,
    val error: ErrorDetail
)

data class ErrorDetail(
    val code: String,
    val message: String,
    val details: Any?
)
