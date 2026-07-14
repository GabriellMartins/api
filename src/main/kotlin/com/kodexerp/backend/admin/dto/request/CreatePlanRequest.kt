package com.kodexerp.backend.admin.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreatePlanRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    
    @field:NotNull(message = "Price is required")
    @field:Positive(message = "Price must be positive")
    val price: BigDecimal,
    
    @field:NotBlank(message = "Interval is required")
    val interval: String,
    
    val features: String? = null,
    
    val maxInvoices: Int? = null,
    
    val maxUsers: Int? = null,
    
    val apiAccess: Boolean = false,
    
    val prioritySupport: Boolean = false,
    
    val isActive: Boolean = true
)
