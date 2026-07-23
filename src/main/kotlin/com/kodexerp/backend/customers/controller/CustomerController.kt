package com.kodexerp.backend.customers.controller

import com.kodexerp.backend.customers.dto.request.CreateCustomerRequest
import com.kodexerp.backend.customers.dto.request.UpdateCustomerRequest
import com.kodexerp.backend.customers.dto.response.CustomerResponse
import com.kodexerp.backend.customers.dto.response.CustomersListResponse
import com.kodexerp.backend.shared.dto.response.MessageResponse
import com.kodexerp.backend.shared.security.JwtUtil
import com.kodexerp.backend.customers.service.CustomerService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/customers")
class CustomerController(
    private val customerService: CustomerService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getCustomers(
        @RequestHeader("Authorization") token: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "false") activeOnly: Boolean
    ): ResponseEntity<CustomersListResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = customerService.getCustomers(userId, page, limit, search, activeOnly)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    fun getCustomerStats(@RequestHeader("Authorization") token: String): ResponseEntity<Map<String, Any>> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = customerService.getCustomerStats(userId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getCustomerById(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: UUID
    ): ResponseEntity<CustomerResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = customerService.getCustomerById(userId, id)
        return ResponseEntity.ok(result)
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createCustomer(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody request: CreateCustomerRequest
    ): ResponseEntity<CustomerResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = customerService.createCustomer(userId, request)
        return ResponseEntity.ok(result)
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun updateCustomer(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateCustomerRequest
    ): ResponseEntity<CustomerResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = customerService.updateCustomer(userId, id, request)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun deleteCustomer(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: UUID
    ): ResponseEntity<MessageResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        customerService.deleteCustomer(userId, id)
        return ResponseEntity.ok(
            MessageResponse(
                success = true,
                message = "Customer deleted successfully"
            )
        )
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("isAuthenticated()")
    fun activateCustomer(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: UUID
    ): ResponseEntity<CustomerResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = customerService.activateCustomer(userId, id)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("isAuthenticated()")
    fun deactivateCustomer(
        @RequestHeader("Authorization") token: String,
        @PathVariable id: UUID
    ): ResponseEntity<CustomerResponse> {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        val result = customerService.deactivateCustomer(userId, id)
        return ResponseEntity.ok(result)
    }
}
