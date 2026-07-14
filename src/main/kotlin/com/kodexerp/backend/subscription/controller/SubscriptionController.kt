package com.kodexerp.backend.subscription.controller

import com.kodexerp.backend.subscription.dto.request.CancelSubscriptionRequest
import com.kodexerp.backend.subscription.dto.request.SubscribeRequest
import com.kodexerp.backend.subscription.dto.request.UpdatePaymentRequest
import com.kodexerp.backend.subscription.dto.response.CurrentSubscriptionResponse
import com.kodexerp.backend.shared.dto.response.MessageResponse
import com.kodexerp.backend.subscription.dto.response.PlansResponse
import com.kodexerp.backend.subscription.dto.response.SubscriptionResponse
import com.kodexerp.backend.shared.security.JwtUtil
import com.kodexerp.backend.subscription.service.SubscriptionService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/subscriptions")
class SubscriptionController(
    private val subscriptionService: SubscriptionService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/plans")
    @PreAuthorize("isAuthenticated()")
    fun getPlans(): ResponseEntity<PlansResponse> {
        val result = subscriptionService.getPlans()
        return ResponseEntity.ok(result)
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentSubscription(@RequestHeader("Authorization") token: String): ResponseEntity<CurrentSubscriptionResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = subscriptionService.getCurrentSubscription(userId)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    fun subscribe(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody request: SubscribeRequest
    ): ResponseEntity<SubscriptionResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = subscriptionService.subscribe(userId, request)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/cancel")
    @PreAuthorize("isAuthenticated()")
    fun cancelSubscription(
        @RequestHeader("Authorization") token: String,
        @RequestBody request: CancelSubscriptionRequest
    ): ResponseEntity<SubscriptionResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = subscriptionService.cancelSubscription(userId, request)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/update-payment")
    @PreAuthorize("isAuthenticated()")
    fun updatePayment(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody request: UpdatePaymentRequest
    ): ResponseEntity<MessageResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = subscriptionService.updatePayment(userId, request)
        return ResponseEntity.ok(result)
    }
}
