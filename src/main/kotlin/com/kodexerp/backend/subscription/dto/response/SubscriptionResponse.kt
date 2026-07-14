package com.kodexerp.backend.subscription.dto.response

import com.kodexerp.backend.subscription.entity.SubscriptionStatus
import java.time.LocalDateTime
import java.util.*

data class SubscriptionResponse(
    val id: UUID,
    val plan: PlanResponse,
    val status: SubscriptionStatus,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val renewalDate: LocalDateTime?,
    val cancelAtPeriodEnd: Boolean,
    val usage: UsageData?
)

data class UsageData(
    val invoicesUsed: Int,
    val invoicesLimit: Int,
    val usersUsed: Int,
    val usersLimit: Int
)
