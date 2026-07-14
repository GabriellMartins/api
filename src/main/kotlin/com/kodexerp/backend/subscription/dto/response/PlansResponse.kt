package com.kodexerp.backend.subscription.dto.response

data class PlansResponse(
    val success: Boolean,
    val data: PlansData
)

data class PlansData(
    val plans: List<PlanResponse>
)
