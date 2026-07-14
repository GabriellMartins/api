package com.kodexerp.backend.subscription.dto.request

data class CancelSubscriptionRequest(
    val cancelAtPeriodEnd: Boolean = true
)
