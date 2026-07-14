package com.kodexerp.backend.admin.dto.request

import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class UpdatePlanRequest(
    val name: String? = null,
    
    @field:Positive(message = "Price must be positive")
    val price: BigDecimal? = null,
    
    val interval: String? = null,
    
    val features: String? = null,
    
    val maxInvoices: Int? = null,
    
    val maxUsers: Int? = null,
    
    val apiAccess: Boolean? = null,
    
    val prioritySupport: Boolean? = null,
    
    val isActive: Boolean? = null
)
