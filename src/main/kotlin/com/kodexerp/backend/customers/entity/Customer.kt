package com.kodexerp.backend.customers.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime
import java.util.*

@Document(collection = "customers")
data class Customer(
    @Id
    val id: UUID? = null,
    
    @Indexed
    val userId: UUID,
    
    val name: String,
    
    @Indexed
    val email: String,
    
    val phone: String?,
    
    val cpfCnpj: String?,
    
    val address: String?,
    
    val city: String?,
    
    val state: String?,
    
    val zipCode: String?,
    
    val country: String? = "Brasil",
    
    val notes: String?,
    
    val isActive: Boolean = true,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
