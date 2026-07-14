package com.kodexerp.backend.subscription.controller

import com.kodexerp.backend.subscription.service.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/plans")
class PublicPlanController(
    private val subscriptionService: SubscriptionService
) {

    @GetMapping
    fun getPublicPlans(): ResponseEntity<Any> {
        val plansResponse = subscriptionService.getPlans()
        return ResponseEntity.ok(
            mapOf(
                "plans" to plansResponse.data.plans.map { plan ->
                    mapOf(
                        "id" to plan.id,
                        "name" to plan.name,
                        "price" to plan.price,
                        "interval" to plan.interval,
                        "description" to getPlanDescription(plan.name),
                        "features" to plan.features,
                        "maxInvoices" to plan.maxInvoices,
                        "maxUsers" to plan.maxUsers,
                        "apiAccess" to plan.apiAccess,
                        "prioritySupport" to plan.prioritySupport,
                        "isPopular" to isPopularPlan(plan.name)
                    )
                }
            )
        )
    }

    private fun getPlanDescription(name: String): String {
        return when (name.lowercase()) {
            "starter" -> "Pra quem tá começando. Tudo que precisa pra começar a emitir notas."
            "pro" -> "Pra quem tá crescendo. Mais recursos e suporte prioritário."
            "enterprise" -> "Pra grandes negócios. Sem limites e suporte dedicado."
            else -> "Plano personalizado para suas necessidades."
        }
    }

    private fun isPopularPlan(name: String): Boolean {
        return name.lowercase() == "pro"
    }
}
