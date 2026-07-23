package com.kodexerp.backend.customers.dto.response

data class CustomersListResponse(
    val success: Boolean,
    val data: CustomersListData
)

data class CustomersListData(
    val customers: List<CustomerResponse>,
    val pagination: PaginationData
)

data class PaginationData(
    val page: Int,
    val limit: Int,
    val total: Long,
    val totalPages: Int
)
