package com.kodexerp.backend.customers.repository

import com.kodexerp.backend.customers.entity.Customer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CustomerRepository : MongoRepository<Customer, UUID> {
    
    fun findByUserId(userId: UUID, pageable: Pageable): Page<Customer>
    
    fun findByUserId(userId: UUID): List<Customer>
    
    fun findByUserIdAndIsActiveTrue(userId: UUID, pageable: Pageable): Page<Customer>
    
    fun findByUserIdAndIsActiveTrue(userId: UUID): List<Customer>
    
    fun findByUserIdAndId(userId: UUID, id: UUID): Customer?
    
    fun findByEmail(email: String): Customer?
    
    fun findByUserIdAndEmail(userId: UUID, email: String): Customer?
    
    fun findByUserIdAndNameContainingIgnoreCase(userId: UUID, name: String, pageable: Pageable): Page<Customer>
    
    fun countByUserId(userId: UUID): Long
    
    fun countByUserIdAndIsActiveTrue(userId: UUID): Long
}
