package com.kodexerp.backend.subscription.service

import com.kodexerp.backend.subscription.dto.request.CancelSubscriptionRequest
import com.kodexerp.backend.subscription.dto.request.SubscribeRequest
import com.kodexerp.backend.subscription.dto.request.UpdatePaymentRequest
import com.kodexerp.backend.subscription.dto.response.CurrentSubscriptionResponse
import com.kodexerp.backend.shared.dto.response.MessageResponse
import com.kodexerp.backend.subscription.dto.response.PlanResponse
import com.kodexerp.backend.subscription.dto.response.PlansData
import com.kodexerp.backend.subscription.dto.response.PlansResponse
import com.kodexerp.backend.subscription.dto.response.SubscriptionResponse
import com.kodexerp.backend.subscription.dto.response.UsageData
import com.kodexerp.backend.subscription.entity.Plan
import com.kodexerp.backend.subscription.entity.Subscription
import com.kodexerp.backend.subscription.entity.SubscriptionStatus
import com.kodexerp.backend.subscription.repository.PlanRepository
import com.kodexerp.backend.subscription.repository.SubscriptionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
    private val planRepository: PlanRepository
) {

    fun getPlans(): PlansResponse {
        val plans = planRepository.findByIsActiveTrueOrderByPriceAsc()
        return PlansResponse(
            success = true,
            data = PlansData(
                plans = plans.map { mapToPlanResponse(it) }
            )
        )
    }

    fun getCurrentSubscription(userId: UUID): CurrentSubscriptionResponse {
        val subscription = subscriptionRepository.findByUserId(userId)
        
        if (subscription == null) {
            return CurrentSubscriptionResponse(
                success = true,
                data = createTrialSubscription(userId)
            )
        }

        return CurrentSubscriptionResponse(
            success = true,
            data = mapToSubscriptionResponse(subscription)
        )
    }

    @Transactional
    fun subscribe(userId: UUID, request: SubscribeRequest): SubscriptionResponse {
        val plan = planRepository.findByIdAndIsActiveTrue(request.planId)
            ?: throw IllegalArgumentException("Plan not found or inactive")

        val existingSubscription = subscriptionRepository.findByUserIdAndStatus(
            userId,
            SubscriptionStatus.ACTIVE
        )
        if (existingSubscription != null) {
            throw IllegalArgumentException("User already has an active subscription")
        }

        if (request.paymentMethod == "credit" && request.cardToken == null) {
            throw IllegalArgumentException("Card token is required for credit payment")
        }

        val (startDate, endDate) = calculateDates(request.interval)

        val subscription = Subscription(
            userId = userId,
            planId = plan.id,
            status = SubscriptionStatus.ACTIVE,
            startDate = startDate,
            endDate = endDate,
            renewalDate = endDate,
            cancelAtPeriodEnd = false,
            paymentMethod = com.kodexerp.backend.subscription.entity.PaymentMethod.valueOf(request.paymentMethod.uppercase()),
            cardToken = request.cardToken
        )

        val savedSubscription = subscriptionRepository.save(subscription)
        return mapToSubscriptionResponse(savedSubscription)
    }

    @Transactional
    fun cancelSubscription(userId: UUID, request: CancelSubscriptionRequest): SubscriptionResponse {
        val subscription = subscriptionRepository.findByUserId(userId)
            ?: throw IllegalArgumentException("No subscription found")

        val updatedSubscription = if (!request.cancelAtPeriodEnd) {
            subscription.copy(
                cancelAtPeriodEnd = request.cancelAtPeriodEnd,
                status = SubscriptionStatus.CANCELLED,
                endDate = LocalDateTime.now()
            )
        } else {
            subscription.copy(
                cancelAtPeriodEnd = request.cancelAtPeriodEnd,
                status = SubscriptionStatus.CANCELLED
            )
        }

        val savedSubscription = subscriptionRepository.save(updatedSubscription)
        return mapToSubscriptionResponse(savedSubscription)
    }

    @Transactional
    fun updatePayment(userId: UUID, request: UpdatePaymentRequest): MessageResponse {
        val subscription = subscriptionRepository.findByUserId(userId)
            ?: throw IllegalArgumentException("No subscription found")

        if (request.paymentMethod == "credit" && request.cardToken == null) {
            throw IllegalArgumentException("Card token is required for credit payment")
        }

        val updatedSubscription = subscription.copy(
            paymentMethod = com.kodexerp.backend.subscription.entity.PaymentMethod.valueOf(request.paymentMethod.uppercase()),
            cardToken = request.cardToken
        )
        subscriptionRepository.save(updatedSubscription)

        return MessageResponse(
            success = true,
            message = "Método de pagamento atualizado com sucesso"
        )
    }

    private fun calculateDates(interval: String): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()
        val endDate = if (interval.lowercase() == "yearly") {
            now.plusYears(1)
        } else {
            now.plusMonths(1)
        }
        return Pair(now, endDate)
    }

    private fun mapToPlanResponse(plan: Plan): PlanResponse {
        return PlanResponse(
            id = plan.id,
            name = plan.name,
            price = plan.price,
            interval = plan.interval,
            features = plan.features?.split(",") ?: emptyList(),
            maxInvoices = plan.maxInvoices,
            maxUsers = plan.maxUsers,
            apiAccess = plan.apiAccess,
            prioritySupport = plan.prioritySupport,
            isActive = plan.isActive
        )
    }

    private fun mapToSubscriptionResponse(subscription: Subscription): SubscriptionResponse {
        val plan = planRepository.findById(subscription.planId)
            .orElseThrow { IllegalArgumentException("Plan not found") }

        return SubscriptionResponse(
            id = subscription.id,
            plan = mapToPlanResponse(plan),
            status = subscription.status,
            startDate = subscription.startDate,
            endDate = subscription.endDate,
            renewalDate = subscription.renewalDate,
            cancelAtPeriodEnd = subscription.cancelAtPeriodEnd,
            usage = calculateUsage(plan)
        )
    }

    private fun createTrialSubscription(userId: UUID): SubscriptionResponse {
        val starterPlan = planRepository.findByIsActiveTrueOrderByPriceAsc()
            .firstOrNull()
            ?: throw IllegalArgumentException("No active plans available")

        return SubscriptionResponse(
            id = UUID.randomUUID(),
            plan = mapToPlanResponse(starterPlan),
            status = SubscriptionStatus.TRIAL,
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusDays(14),
            renewalDate = null,
            cancelAtPeriodEnd = false,
            usage = UsageData(
                invoicesUsed = 0,
                invoicesLimit = starterPlan.maxInvoices ?: 10,
                usersUsed = 1,
                usersLimit = starterPlan.maxUsers ?: 1
            )
        )
    }

    private fun calculateUsage(plan: Plan): UsageData {
        return UsageData(
            invoicesUsed = 0,
            invoicesLimit = plan.maxInvoices ?: 0,
            usersUsed = 1,
            usersLimit = plan.maxUsers ?: 0
        )
    }
}
