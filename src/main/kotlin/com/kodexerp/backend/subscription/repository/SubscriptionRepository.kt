package com.kodexerp.backend.subscription.repository

import com.kodexerp.backend.subscription.entity.Subscription
import com.kodexerp.backend.subscription.entity.SubscriptionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubscriptionRepository : MongoRepository<Subscription, UUID> {
    fun findByUserId(userId: UUID): Subscription?
    fun findByUserIdAndStatus(userId: UUID, status: SubscriptionStatus): Subscription?
    fun findByStatus(status: SubscriptionStatus): List<Subscription>
    fun findByStatus(status: SubscriptionStatus, pageable: Pageable): Page<Subscription>
    fun countByStatus(status: SubscriptionStatus): Long
}
