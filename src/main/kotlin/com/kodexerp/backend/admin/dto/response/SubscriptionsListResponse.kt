package com.kodexerp.backend.admin.dto.response

import com.kodexerp.backend.subscription.entity.SubscriptionStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class SubscriptionsListResponse(
    val success: Boolean,
    val data: SubscriptionsListData
)

data class SubscriptionsListData(
    val subscriptions: List<SubscriptionListItem>,
    val pagination: PaginationData
)

data class SubscriptionListItem(
    val id: UUID,
    val userId: UUID,
    val userName: String,
    val userEmail: String,
    val plan: String,
    val status: SubscriptionStatus,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val renewalDate: LocalDateTime?,
    val amount: BigDecimal
)
