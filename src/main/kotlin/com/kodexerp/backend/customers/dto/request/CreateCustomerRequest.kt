package com.kodexerp.backend.customers.dto.request

import com.kodexerp.backend.shared.util.InputSanitizer
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateCustomerRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    @field:Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-']+$", message = "Name contains invalid characters")
    val name: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,
    
    @field:Size(max = 20, message = "Phone must be at most 20 characters")
    @field:Pattern(regexp = "^[0-9+()\\-\\s]*$", message = "Phone contains invalid characters")
    val phone: String?,
    
    @field:Size(max = 20, message = "CPF/CNPJ must be at most 20 characters")
    @field:Pattern(regexp = "^[0-9]*$", message = "CPF/CNPJ must contain only numbers")
    val cpfCnpj: String?,
    
    @field:Size(max = 500, message = "Address must be at most 500 characters")
    @field:Pattern(regexp = "^[a-zA-Z0-9À-ÿ\\s\\-.,']*$", message = "Address contains invalid characters")
    val address: String?,
    
    @field:Size(max = 100, message = "City must be at most 100 characters")
    @field:Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-']*$", message = "City contains invalid characters")
    val city: String?,
    
    @field:Size(max = 2, message = "State must be at most 2 characters")
    @field:Pattern(regexp = "^[A-Z]{2}$", message = "State must be 2 uppercase letters")
    val state: String?,
    
    @field:Size(max = 10, message = "Zip code must be at most 10 characters")
    @field:Pattern(regexp = "^[0-9\\-]*$", message = "Zip code contains invalid characters")
    val zipCode: String?,
    
    @field:Size(max = 50, message = "Country must be at most 50 characters")
    @field:Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-']*$", message = "Country contains invalid characters")
    val country: String?,
    
    @field:Size(max = 1000, message = "Notes must be at most 1000 characters")
    val notes: String?
) {
    fun sanitize(): CreateCustomerRequest {
        return CreateCustomerRequest(
            name = InputSanitizer.sanitizeAllowSimpleText(name),
            email = InputSanitizer.sanitizeEmail(email),
            phone = phone?.let { InputSanitizer.sanitizePhone(it) },
            cpfCnpj = cpfCnpj?.let { InputSanitizer.sanitizeCpfCnpj(it) },
            address = address?.let { InputSanitizer.sanitizeAllowSimpleText(it) },
            city = city?.let { InputSanitizer.sanitizeAllowSimpleText(it) },
            state = state,
            zipCode = zipCode?.let { InputSanitizer.sanitizeZipCode(it) },
            country = country?.let { InputSanitizer.sanitizeAllowSimpleText(it) },
            notes = notes?.let { InputSanitizer.sanitizeAllowSimpleText(it) }
        )
    }
}
