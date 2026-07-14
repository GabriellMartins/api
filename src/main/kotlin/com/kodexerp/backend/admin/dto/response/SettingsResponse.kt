package com.kodexerp.backend.admin.dto.response

data class SettingsResponse(
    val success: Boolean,
    val data: SettingsData
)

data class SettingsData(
    val maintenanceMode: Boolean,
    val registrationEnabled: Boolean,
    val maxUsersPerPlan: Map<String, Int>
)
