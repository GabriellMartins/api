package com.kodexerp.backend.subscription.dto.response

import com.kodexerp.backend.subscription.entity.PlanInterval
import java.math.BigDecimal
import java.util.*

data class PlanResponse(
    val id: UUID,
    val name: String,
    val price: BigDecimal,
    val interval: PlanInterval,
    val features: List<String>,
    val maxInvoices: Int?,
    val maxUsers: Int?,
    val apiAccess: Boolean,
    val prioritySupport: Boolean,
    val isActive: Boolean
)
