package com.kodexerp.backend.admin.service

import com.kodexerp.backend.admin.dto.request.CreatePlanRequest
import com.kodexerp.backend.admin.dto.request.UpdatePlanRequest
import com.kodexerp.backend.subscription.dto.response.PlanResponse
import com.kodexerp.backend.subscription.entity.Plan
import com.kodexerp.backend.subscription.entity.PlanInterval
import com.kodexerp.backend.subscription.repository.PlanRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
class PlanService(
    private val planRepository: PlanRepository
) {

    fun getAllPlans(): List<PlanResponse> {
        return planRepository.findAll().map { mapToPlanResponse(it) }
    }

    fun getPlanById(id: UUID): PlanResponse {
        val plan = planRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Plan not found") }
        return mapToPlanResponse(plan)
    }

    @Transactional
    fun createPlan(request: CreatePlanRequest): PlanResponse {
        val plan = Plan(
            name = request.name,
            price = request.price,
            interval = PlanInterval.valueOf(request.interval.uppercase()),
            features = request.features,
            maxInvoices = request.maxInvoices,
            maxUsers = request.maxUsers,
            apiAccess = request.apiAccess,
            prioritySupport = request.prioritySupport,
            isActive = request.isActive
        )
        val savedPlan = planRepository.save(plan)
        return mapToPlanResponse(savedPlan)
    }

    @Transactional
    fun updatePlan(id: UUID, request: UpdatePlanRequest): PlanResponse {
        val plan = planRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Plan not found") }

        val updatedPlan = plan.copy(
            name = request.name ?: plan.name,
            price = request.price ?: plan.price,
            interval = request.interval?.let { PlanInterval.valueOf(it.uppercase()) } ?: plan.interval,
            features = request.features ?: plan.features,
            maxInvoices = request.maxInvoices ?: plan.maxInvoices,
            maxUsers = request.maxUsers ?: plan.maxUsers,
            apiAccess = request.apiAccess ?: plan.apiAccess,
            prioritySupport = request.prioritySupport ?: plan.prioritySupport,
            isActive = request.isActive ?: plan.isActive
        )

        val savedPlan = planRepository.save(updatedPlan)
        return mapToPlanResponse(savedPlan)
    }

    @Transactional
    fun deletePlan(id: UUID) {
        if (!planRepository.existsById(id)) {
            throw IllegalArgumentException("Plan not found")
        }
        planRepository.deleteById(id)
    }

    @Transactional
    fun initializeDefaultPlans() {
        if (planRepository.count() > 0) {
            return // Plans already initialized
        }

        val plans = listOf(
            Plan(
                name = "Starter",
                price = BigDecimal("49.00"),
                interval = PlanInterval.MONTHLY,
                features = "Até 50 notas por mês,Suporte por email,API básica,Relatórios simples,1 usuário",
                maxInvoices = 50,
                maxUsers = 1,
                apiAccess = true,
                prioritySupport = false,
                isActive = true
            ),
            Plan(
                name = "Starter",
                price = BigDecimal("470.40"),
                interval = PlanInterval.YEARLY,
                features = "Até 50 notas por mês,Suporte por email,API básica,Relatórios simples,1 usuário,20% de desconto anual",
                maxInvoices = 50,
                maxUsers = 1,
                apiAccess = true,
                prioritySupport = false,
                isActive = true
            ),
            Plan(
                name = "Pro",
                price = BigDecimal("149.00"),
                interval = PlanInterval.MONTHLY,
                features = "Até 500 notas por mês,Suporte prioritário,API completa,Relatórios avançados,5 usuários,Webhooks,Integrações",
                maxInvoices = 500,
                maxUsers = 5,
                apiAccess = true,
                prioritySupport = true,
                isActive = true
            ),
            Plan(
                name = "Pro",
                price = BigDecimal("1430.40"),
                interval = PlanInterval.YEARLY,
                features = "Até 500 notas por mês,Suporte prioritário,API completa,Relatórios avançados,5 usuários,Webhooks,Integrações,20% de desconto anual",
                maxInvoices = 500,
                maxUsers = 5,
                apiAccess = true,
                prioritySupport = true,
                isActive = true
            ),
            // Enterprise Plans
            Plan(
                name = "Enterprise",
                price = BigDecimal("399.00"),
                interval = PlanInterval.MONTHLY,
                features = "Notas ilimitadas,Suporte dedicado 24/7,API personalizada,Relatórios customizados,Usuários ilimitados,SLA garantido,Gerente de conta,Treinamento da equipe",
                maxInvoices = null,
                maxUsers = null,
                apiAccess = true,
                prioritySupport = true,
                isActive = true
            ),
            Plan(
                name = "Enterprise",
                price = BigDecimal("3830.40"),
                interval = PlanInterval.YEARLY,
                features = "Notas ilimitadas,Suporte dedicado 24/7,API personalizada,Relatórios customizados,Usuários ilimitados,SLA garantido,Gerente de conta,Treinamento da equipe,20% de desconto anual (R$ 399 x 12 = R$ 4.788 - 20% = R$ 3.830,40)",
                maxInvoices = null,
                maxUsers = null,
                apiAccess = true,
                prioritySupport = true,
                isActive = true
            )
        )

        planRepository.saveAll(plans)
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
}
