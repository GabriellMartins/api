package com.kodexerp.backend.subscription.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.util.*

@Document(collection = "plans")
data class Plan(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Indexed
    val name: String,

    val price: BigDecimal,

    val interval: PlanInterval,

    val features: String? = null,

    val maxInvoices: Int? = null,

    val maxUsers: Int? = null,

    val apiAccess: Boolean = false,

    val prioritySupport: Boolean = false,

    @Indexed
    val isActive: Boolean = true
)

enum class PlanInterval {
    MONTHLY,
    YEARLY
}
