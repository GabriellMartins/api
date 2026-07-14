package com.kodexerp.backend.admin.dto.response

import java.math.BigDecimal

data class RevenueResponse(
    val success: Boolean,
    val data: RevenueData
)

data class RevenueData(
    val totalRevenue: BigDecimal,
    val revenueByPeriod: List<RevenueByPeriod>,
    val revenueByPlan: List<RevenueByPlan>
)

data class RevenueByPeriod(
    val period: String,
    val revenue: BigDecimal,
    val subscriptions: Long
)

data class RevenueByPlan(
    val plan: String,
    val revenue: BigDecimal,
    val count: Long
)
