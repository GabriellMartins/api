package com.kodexerp.backend.customers.service

import com.kodexerp.backend.customers.dto.request.CreateCustomerRequest
import com.kodexerp.backend.customers.dto.request.UpdateCustomerRequest
import com.kodexerp.backend.customers.dto.response.CustomerResponse
import com.kodexerp.backend.customers.dto.response.CustomersListResponse
import com.kodexerp.backend.customers.entity.Customer
import com.kodexerp.backend.customers.repository.CustomerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
) {

    fun getCustomers(
        userId: UUID,
        page: Int = 1,
        limit: Int = 20,
        search: String? = null,
        activeOnly: Boolean = false
    ): CustomersListResponse {
        val pageable: Pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        
        val customersPage: Page<Customer> = if (search != null) {
            customerRepository.findByUserIdAndNameContainingIgnoreCase(userId, search, pageable)
        } else if (activeOnly) {
            customerRepository.findByUserIdAndIsActiveTrue(userId, pageable)
        } else {
            customerRepository.findByUserId(userId, pageable)
        }

        return CustomersListResponse(
            success = true,
            data = com.kodexerp.backend.customers.dto.response.CustomersListData(
                customers = customersPage.content.map { mapToCustomerResponse(it) },
                pagination = com.kodexerp.backend.customers.dto.response.PaginationData(
                    page = page,
                    limit = limit,
                    total = customersPage.totalElements,
                    totalPages = customersPage.totalPages
                )
            )
        )
    }

    fun getCustomerById(userId: UUID, id: UUID): CustomerResponse {
        val customer = customerRepository.findByUserIdAndId(userId, id)
            ?: throw IllegalArgumentException("Customer not found")
        return mapToCustomerResponse(customer)
    }

    @Transactional
    fun createCustomer(userId: UUID, request: CreateCustomerRequest): CustomerResponse {
        try {
            val sanitizedRequest = request.sanitize()
            
            customerRepository.findByUserIdAndEmail(userId, sanitizedRequest.email)?.let {
                throw IllegalArgumentException("Customer with this email already exists")
            }

            val customer = Customer(
                id = UUID.randomUUID(),
                userId = userId,
                name = sanitizedRequest.name,
                email = sanitizedRequest.email,
                phone = sanitizedRequest.phone,
                cpfCnpj = sanitizedRequest.cpfCnpj,
                address = sanitizedRequest.address,
                city = sanitizedRequest.city,
                state = sanitizedRequest.state,
                zipCode = sanitizedRequest.zipCode,
                country = sanitizedRequest.country ?: "Brasil",
                notes = sanitizedRequest.notes,
                isActive = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            val savedCustomer = customerRepository.save(customer)
            return mapToCustomerResponse(savedCustomer)
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("Failed to create customer: ${e.message}", e)
        }
    }

    @Transactional
    fun updateCustomer(userId: UUID, id: UUID, request: UpdateCustomerRequest): CustomerResponse {
        val customer = customerRepository.findByUserIdAndId(userId, id)
            ?: throw IllegalArgumentException("Customer not found")

        request.email?.let { newEmail ->
            if (newEmail != customer.email) {
                customerRepository.findByUserIdAndEmail(userId, newEmail)?.let {
                    throw IllegalArgumentException("Customer with this email already exists")
                }
            }
        }

        val updatedCustomer = customer.copy(
            name = request.name ?: customer.name,
            email = request.email ?: customer.email,
            phone = request.phone ?: customer.phone,
            cpfCnpj = request.cpfCnpj ?: customer.cpfCnpj,
            address = request.address ?: customer.address,
            city = request.city ?: customer.city,
            state = request.state ?: customer.state,
            zipCode = request.zipCode ?: customer.zipCode,
            country = request.country ?: customer.country,
            notes = request.notes ?: customer.notes,
            isActive = request.isActive ?: customer.isActive,
            updatedAt = LocalDateTime.now()
        )

        val savedCustomer = customerRepository.save(updatedCustomer)
        return mapToCustomerResponse(savedCustomer)
    }

    @Transactional
    fun deleteCustomer(userId: UUID, id: UUID) {
        val customer = customerRepository.findByUserIdAndId(userId, id)
            ?: throw IllegalArgumentException("Customer not found")
        customerRepository.delete(customer)
    }

    @Transactional
    fun activateCustomer(userId: UUID, id: UUID): CustomerResponse {
        val customer = customerRepository.findByUserIdAndId(userId, id)
            ?: throw IllegalArgumentException("Customer not found")

        val updatedCustomer = customer.copy(
            isActive = true,
            updatedAt = LocalDateTime.now()
        )

        val savedCustomer = customerRepository.save(updatedCustomer)
        return mapToCustomerResponse(savedCustomer)
    }

    @Transactional
    fun deactivateCustomer(userId: UUID, id: UUID): CustomerResponse {
        val customer = customerRepository.findByUserIdAndId(userId, id)
            ?: throw IllegalArgumentException("Customer not found")

        val updatedCustomer = customer.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )

        val savedCustomer = customerRepository.save(updatedCustomer)
        return mapToCustomerResponse(savedCustomer)
    }

    fun getCustomerStats(userId: UUID): Map<String, Any> {
        val totalCustomers = customerRepository.countByUserId(userId)
        val activeCustomers = customerRepository.countByUserIdAndIsActiveTrue(userId)
        
        return mapOf(
            "totalCustomers" to totalCustomers,
            "activeCustomers" to activeCustomers,
            "inactiveCustomers" to (totalCustomers - activeCustomers)
        )
    }

    private fun mapToCustomerResponse(customer: Customer): CustomerResponse {
        return CustomerResponse(
            id = customer.id ?: throw IllegalStateException("Customer ID cannot be null"),
            userId = customer.userId,
            name = customer.name,
            email = customer.email,
            phone = customer.phone,
            cpfCnpj = customer.cpfCnpj,
            address = customer.address,
            city = customer.city,
            state = customer.state,
            zipCode = customer.zipCode,
            country = customer.country,
            notes = customer.notes,
            isActive = customer.isActive,
            createdAt = customer.createdAt,
            updatedAt = customer.updatedAt
        )
    }
}
