package com.kodexerp.backend.subscription.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Document(collection = "subscriptions")
data class Subscription(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Indexed
    val userId: UUID,

    @Indexed
    val planId: UUID,

    @Indexed
    val status: SubscriptionStatus,

    val startDate: LocalDateTime,

    val endDate: LocalDateTime,

    val renewalDate: LocalDateTime? = null,

    val cancelAtPeriodEnd: Boolean = false,

    val paymentMethod: PaymentMethod,

    val cardToken: String? = null,

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class SubscriptionStatus {
    ACTIVE,
    CANCELLED,
    EXPIRED,
    TRIAL
}

enum class PaymentMethod {
    CREDIT,
    BOLETO,
    PIX
}
