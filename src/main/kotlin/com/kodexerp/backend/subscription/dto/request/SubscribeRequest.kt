package com.kodexerp.backend.subscription.dto.request

import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class SubscribeRequest(
    @field:NotBlank(message = "Plan ID is required")
    val planId: UUID,
    
    @field:NotBlank(message = "Payment method is required")
    val paymentMethod: String,
    
    @field:NotBlank(message = "Interval is required")
    val interval: String,
    
    val cardToken: String? = null
)
