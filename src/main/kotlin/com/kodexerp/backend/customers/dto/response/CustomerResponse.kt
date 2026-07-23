package com.kodexerp.backend.customers.dto.response

import java.time.LocalDateTime
import java.util.*

data class CustomerResponse(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val email: String,
    val phone: String?,
    val cpfCnpj: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val country: String?,
    val notes: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
