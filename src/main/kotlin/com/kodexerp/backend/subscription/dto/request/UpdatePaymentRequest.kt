package com.kodexerp.backend.subscription.dto.request

import jakarta.validation.constraints.NotBlank

data class UpdatePaymentRequest(
    @field:NotBlank(message = "Payment method is required")
    val paymentMethod: String,
    
    val cardToken: String? = null
)
