package com.kodexerp.backend.admin.service

import com.kodexerp.backend.admin.dto.request.UpdateSettingsRequest
import com.kodexerp.backend.admin.dto.response.*
import com.kodexerp.backend.subscription.entity.Plan
import com.kodexerp.backend.subscription.entity.Subscription
import com.kodexerp.backend.subscription.entity.SubscriptionStatus
import com.kodexerp.backend.auth.entity.User
import com.kodexerp.backend.auth.entity.UserRole
import com.kodexerp.backend.subscription.repository.PlanRepository
import com.kodexerp.backend.admin.repository.SettingsRepository
import com.kodexerp.backend.subscription.repository.SubscriptionRepository
import com.kodexerp.backend.auth.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.*

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val planRepository: PlanRepository,
    private val settingsRepository: SettingsRepository
) {

    fun getDashboard(): DashboardResponse {
        val totalUsers = userRepository.count()
        val activeUsers = userRepository.countByIsActiveTrue()
        val totalSubscriptions = subscriptionRepository.count()
        val activeSubscriptions = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE)
        
        val totalRevenue = calculateTotalRevenue()
        val monthlyRevenue = calculateMonthlyRevenue()
        val monthlySubscriptions = calculateMonthlySubscriptions()
        
        val growthRate = if (monthlyRevenue > BigDecimal.ZERO) {
            val lastMonthRevenue = calculateRevenueForMonth(LocalDateTime.now().minusMonths(1))
            if (lastMonthRevenue > BigDecimal.ZERO) {
                ((monthlyRevenue - lastMonthRevenue) / lastMonthRevenue) * BigDecimal("100")
            } else BigDecimal("100")
        } else BigDecimal("0")

        val recentUsers = userRepository.findAll(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).content.map { mapToRecentUser(it) }

        val revenueChart = generateRevenueChart()

        return DashboardResponse(
            success = true,
            data = DashboardData(
                totalUsers = totalUsers,
                activeUsers = activeUsers,
                totalRevenue = totalRevenue,
                monthlyRevenue = monthlyRevenue,
                totalSubscriptions = totalSubscriptions,
                monthlySubscriptions = monthlySubscriptions,
                growthRate = growthRate,
                recentUsers = recentUsers,
                revenueChart = revenueChart
            )
        )
    }

    fun getUsers(page: Int, limit: Int, search: String?, role: String?, status: String?): UsersListResponse {
        val pageable: Pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        
        val roleEnum = role?.let { UserRole.valueOf(it.uppercase()) }
        val statusBoolean = status?.let { it.toBoolean() }

        val usersPage = if (roleEnum != null && statusBoolean != null) {
            userRepository.findByRoleAndIsActive(roleEnum, statusBoolean, pageable)
        } else {
            userRepository.findAll(pageable)
        }

        return UsersListResponse(
            success = true,
            data = UsersListData(
                users = usersPage.content.map { mapToUserListItem(it) },
                pagination = PaginationData(
                    page = page,
                    limit = limit,
                    total = usersPage.totalElements,
                    totalPages = usersPage.totalPages
                )
            )
        )
    }

    fun getSubscriptions(page: Int, limit: Int, status: String?, plan: String?): SubscriptionsListResponse {
        val pageable: Pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        
        val statusEnum = status?.let { SubscriptionStatus.valueOf(it.uppercase()) }

        val subscriptionsPage = if (statusEnum != null) {
            subscriptionRepository.findByStatus(statusEnum, pageable)
        } else {
            subscriptionRepository.findAll(pageable)
        }

        return SubscriptionsListResponse(
            success = true,
            data = SubscriptionsListData(
                subscriptions = subscriptionsPage.content.map { mapToSubscriptionListItem(it) },
                pagination = PaginationData(
                    page = page,
                    limit = limit,
                    total = subscriptionsPage.totalElements,
                    totalPages = subscriptionsPage.totalPages
                )
            )
        )
    }

    fun getRevenue(startDate: LocalDateTime?, endDate: LocalDateTime?, groupBy: String): RevenueResponse {
        val start = startDate ?: LocalDateTime.now().minusMonths(12)
        val end = endDate ?: LocalDateTime.now()

        val subscriptions = subscriptionRepository.findAll().filter { 
            it.startDate.isAfter(start) && it.startDate.isBefore(end) 
        }

        val totalRevenue = subscriptions.sumOf { sub ->
            planRepository.findById(sub.planId).get().price
        }

        val revenueByPeriod = when (groupBy.lowercase()) {
            "day" -> groupRevenueByDay(subscriptions, start, end)
            "week" -> groupRevenueByWeek(subscriptions, start, end)
            else -> groupRevenueByMonth(subscriptions, start, end)
        }

        val revenueByPlan = groupRevenueByPlan(subscriptions)

        return RevenueResponse(
            success = true,
            data = RevenueData(
                totalRevenue = totalRevenue,
                revenueByPeriod = revenueByPeriod,
                revenueByPlan = revenueByPlan
            )
        )
    }

    fun getSettings(): SettingsResponse {
        val maintenanceMode = getSettingValue("maintenanceMode", "false").toBoolean()
        val registrationEnabled = getSettingValue("registrationEnabled", "true").toBoolean()
        
        val maxUsersPerPlan = mapOf(
            "starter" to getSettingValue("maxUsersPerPlan.starter", "5").toInt(),
            "pro" to getSettingValue("maxUsersPerPlan.pro", "20").toInt(),
            "enterprise" to getSettingValue("maxUsersPerPlan.enterprise", "100").toInt()
        )

        return SettingsResponse(
            success = true,
            data = SettingsData(
                maintenanceMode = maintenanceMode,
                registrationEnabled = registrationEnabled,
                maxUsersPerPlan = maxUsersPerPlan
            )
        )
    }

    @Transactional
    fun updateSettings(request: UpdateSettingsRequest): SettingsResponse {
        request.maintenanceMode?.let {
            saveSetting("maintenanceMode", it.toString())
        }
        request.registrationEnabled?.let {
            saveSetting("registrationEnabled", it.toString())
        }
        request.maxUsersPerPlan?.forEach { (plan, maxUsers) ->
            saveSetting("maxUsersPerPlan.$plan", maxUsers.toString())
        }

        return getSettings()
    }

    private fun calculateTotalRevenue(): BigDecimal {
        val activeSubscriptions = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE)
        return activeSubscriptions.sumOf { sub ->
            planRepository.findById(sub.planId).get().price
        }
    }

    private fun calculateMonthlyRevenue(): BigDecimal {
        val now = LocalDateTime.now()
        val monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        
        return subscriptionRepository.findAll().filter { 
            it.startDate.isAfter(monthStart) && it.startDate.isBefore(now)
        }.sumOf { sub ->
            planRepository.findById(sub.planId).get().price
        }
    }

    private fun calculateMonthlySubscriptions(): Long {
        val now = LocalDateTime.now()
        val monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        
        return subscriptionRepository.findAll().count { 
            it.startDate.isAfter(monthStart) && it.startDate.isBefore(now)
        }.toLong()
    }

    private fun calculateRevenueForMonth(date: LocalDateTime): BigDecimal {
        val monthStart = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        val monthEnd = YearMonth.from(date).atEndOfMonth().atTime(23, 59, 59)
        
        return subscriptionRepository.findAll().filter { 
            it.startDate.isAfter(monthStart) && it.startDate.isBefore(monthEnd)
        }.sumOf { sub ->
            planRepository.findById(sub.planId).get().price
        }
    }

    private fun generateRevenueChart(): List<RevenueChartPoint> {
        val chart = mutableListOf<RevenueChartPoint>()
        var currentDate = LocalDateTime.now().minusMonths(11)
        
        repeat(12) {
            val revenue = calculateRevenueForMonth(currentDate)
            chart.add(RevenueChartPoint(
                month = currentDate.month.name.lowercase().capitalize(),
                revenue = revenue
            ))
            currentDate = currentDate.plusMonths(1)
        }
        
        return chart
    }

    private fun groupRevenueByMonth(subscriptions: List<Subscription>, start: LocalDateTime, end: LocalDateTime): List<RevenueByPeriod> {
        val grouped = mutableMapOf<String, RevenueByPeriod>()
        var current = start.withDayOfMonth(1)
        
        while (current.isBefore(end)) {
            val monthKey = "${current.year}-${current.monthValue}"
            grouped[monthKey] = RevenueByPeriod(
                period = monthKey,
                revenue = BigDecimal.ZERO,
                subscriptions = 0L
            )
            current = current.plusMonths(1)
        }

        subscriptions.forEach { sub ->
            val monthKey = "${sub.startDate.year}-${sub.startDate.monthValue}"
            val plan = planRepository.findById(sub.planId).get()
            grouped[monthKey]?.let {
                grouped[monthKey] = it.copy(
                    revenue = it.revenue.add(plan.price),
                    subscriptions = it.subscriptions + 1
                )
            }
        }

        return grouped.values.toList()
    }

    private fun groupRevenueByWeek(subscriptions: List<Subscription>, start: LocalDateTime, end: LocalDateTime): List<RevenueByPeriod> {
        return emptyList()
    }

    private fun groupRevenueByDay(subscriptions: List<Subscription>, start: LocalDateTime, end: LocalDateTime): List<RevenueByPeriod> {
        return emptyList()
    }

    private fun groupRevenueByPlan(subscriptions: List<Subscription>): List<RevenueByPlan> {
        val grouped = mutableMapOf<String, RevenueByPlan>()
        
        subscriptions.forEach { sub ->
            val plan = planRepository.findById(sub.planId).get()
            val key = plan.name
            grouped[key]?.let {
                grouped[key] = it.copy(
                    revenue = it.revenue.add(plan.price),
                    count = it.count + 1
                )
            } ?: run {
                grouped[key] = RevenueByPlan(
                    plan = plan.name,
                    revenue = plan.price,
                    count = 1L
                )
            }
        }

        return grouped.values.toList()
    }

    private fun mapToRecentUser(user: User): RecentUser {
        return RecentUser(
            id = user.id,
            name = user.name,
            email = user.email,
            createdAt = user.createdAt
        )
    }

    private fun mapToUserListItem(user: User): UserListItem {
        val subscription = subscriptionRepository.findByUserId(user.id)
        val planName = subscription?.let { 
            planRepository.findById(it.planId).get().name 
        }

        return UserListItem(
            id = user.id,
            name = user.name,
            email = user.email,
            phone = user.phone,
            company = user.company,
            role = user.role,
            isActive = user.isActive,
            plan = planName,
            createdAt = user.createdAt,
            lastLogin = user.lastLogin
        )
    }

    private fun mapToSubscriptionListItem(subscription: Subscription): SubscriptionListItem {
        val user = userRepository.findById(subscription.userId).get()
        val plan = planRepository.findById(subscription.planId).get()

        return SubscriptionListItem(
            id = subscription.id,
            userId = subscription.userId,
            userName = user.name,
            userEmail = user.email,
            plan = plan.name,
            status = subscription.status,
            startDate = subscription.startDate,
            endDate = subscription.endDate,
            renewalDate = subscription.renewalDate,
            amount = plan.price
        )
    }

    private fun getSettingValue(key: String, defaultValue: String): String {
        return settingsRepository.findByKey(key)?.value ?: defaultValue
    }

    private fun saveSetting(key: String, value: String) {
        val setting = settingsRepository.findByKey(key)
        if (setting != null) {
            settingsRepository.save(setting.copy(value = value))
        } else {
            settingsRepository.save(com.kodexerp.backend.admin.entity.Settings(key = key, value = value))
        }
    }
}

fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
