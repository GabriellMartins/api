package com.kodexerp.backend.admin.dto.request

data class UpdateSettingsRequest(
    val maintenanceMode: Boolean? = null,
    val registrationEnabled: Boolean? = null,
    val maxUsersPerPlan: Map<String, Int>? = null
)
