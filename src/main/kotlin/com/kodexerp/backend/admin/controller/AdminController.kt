package com.kodexerp.backend.admin.controller

import com.kodexerp.backend.admin.dto.request.UpdateSettingsRequest
import com.kodexerp.backend.admin.dto.response.*
import com.kodexerp.backend.admin.service.AdminService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService
) {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    fun getDashboard(): ResponseEntity<DashboardResponse> {
        val result = adminService.getDashboard()
        return ResponseEntity.ok(result)
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    fun getUsers(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) role: String?,
        @RequestParam(required = false) status: String?
    ): ResponseEntity<UsersListResponse> {
        val result = adminService.getUsers(page, limit, search, role, status)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    fun getSubscriptions(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) plan: String?
    ): ResponseEntity<SubscriptionsListResponse> {
        val result = adminService.getSubscriptions(page, limit, status, plan)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    fun getRevenue(
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?,
        @RequestParam(defaultValue = "month") groupBy: String
    ): ResponseEntity<RevenueResponse> {
        val start = startDate?.let { java.time.LocalDateTime.parse(it) }
        val end = endDate?.let { java.time.LocalDateTime.parse(it) }
        val result = adminService.getRevenue(start, end, groupBy)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    fun getSettings(): ResponseEntity<SettingsResponse> {
        val result = adminService.getSettings()
        return ResponseEntity.ok(result)
    }

    @PutMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateSettings(@Valid @RequestBody request: UpdateSettingsRequest): ResponseEntity<SettingsResponse> {
        val result = adminService.updateSettings(request)
        return ResponseEntity.ok(result)
    }
}
