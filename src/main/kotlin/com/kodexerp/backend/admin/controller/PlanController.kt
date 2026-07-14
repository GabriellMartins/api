package com.kodexerp.backend.admin.controller

import com.kodexerp.backend.admin.dto.request.CreatePlanRequest
import com.kodexerp.backend.admin.dto.request.UpdatePlanRequest
import com.kodexerp.backend.admin.service.PlanService
import com.kodexerp.backend.shared.dto.response.MessageResponse
import com.kodexerp.backend.subscription.dto.response.PlanResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/admin/plans")
class PlanController(
    private val planService: PlanService
) {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllPlans(): ResponseEntity<List<PlanResponse>> {
        val plans = planService.getAllPlans()
        return ResponseEntity.ok(plans)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getPlanById(@PathVariable id: UUID): ResponseEntity<PlanResponse> {
        val plan = planService.getPlanById(id)
        return ResponseEntity.ok(plan)
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createPlan(@Valid @RequestBody request: CreatePlanRequest): ResponseEntity<PlanResponse> {
        val plan = planService.createPlan(request)
        return ResponseEntity.ok(plan)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updatePlan(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePlanRequest
    ): ResponseEntity<PlanResponse> {
        val plan = planService.updatePlan(id, request)
        return ResponseEntity.ok(plan)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deletePlan(@PathVariable id: UUID): ResponseEntity<MessageResponse> {
        planService.deletePlan(id)
        return ResponseEntity.ok(
            MessageResponse(
                success = true,
                message = "Plan deleted successfully"
            )
        )
    }

    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    fun initializeDefaultPlans(): ResponseEntity<MessageResponse> {
        planService.initializeDefaultPlans()
        return ResponseEntity.ok(
            MessageResponse(
                success = true,
                message = "Default plans initialized successfully"
            )
        )
    }
}
