package com.kodexerp.backend.customers.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateCustomerRequest(
    @field:Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    val name: String?,
    
    @field:Email(message = "Invalid email format")
    val email: String?,
    
    @field:Size(max = 20, message = "Phone must be at most 20 characters")
    val phone: String?,
    
    @field:Size(max = 20, message = "CPF/CNPJ must be at most 20 characters")
    val cpfCnpj: String?,
    
    @field:Size(max = 500, message = "Address must be at most 500 characters")
    val address: String?,
    
    @field:Size(max = 100, message = "City must be at most 100 characters")
    val city: String?,
    
    @field:Size(max = 2, message = "State must be at most 2 characters")
    val state: String?,
    
    @field:Size(max = 10, message = "Zip code must be at most 10 characters")
    val zipCode: String?,
    
    @field:Size(max = 50, message = "Country must be at most 50 characters")
    val country: String?,
    
    @field:Size(max = 1000, message = "Notes must be at most 1000 characters")
    val notes: String?,
    
    val isActive: Boolean?
)
