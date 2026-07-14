package com.kodexerp.backend.admin.dto.response

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class DashboardResponse(
    val success: Boolean,
    val data: DashboardData
)

data class DashboardData(
    val totalUsers: Long,
    val activeUsers: Long,
    val totalRevenue: BigDecimal,
    val monthlyRevenue: BigDecimal,
    val totalSubscriptions: Long,
    val monthlySubscriptions: Long,
    val growthRate: BigDecimal,
    val recentUsers: List<RecentUser>,
    val revenueChart: List<RevenueChartPoint>
)

data class RecentUser(
    val id: UUID,
    val name: String,
    val email: String,
    val createdAt: LocalDateTime
)

data class RevenueChartPoint(
    val month: String,
    val revenue: BigDecimal
)
