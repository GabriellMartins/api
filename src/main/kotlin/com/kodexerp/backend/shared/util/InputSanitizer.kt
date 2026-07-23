package com.kodexerp.backend.shared.util

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

object InputSanitizer {

    fun sanitize(input: String?): String {
        if (input.isNullOrBlank()) return ""
        
        return Jsoup.clean(input, Safelist.none())
            .trim()
    }

    fun sanitizeAllowBasicFormatting(input: String?): String {
        if (input.isNullOrBlank()) return ""
        
        return Jsoup.clean(input, Safelist.basic())
            .trim()
    }

    fun sanitizeAllowSimpleText(input: String?): String {
        if (input.isNullOrBlank()) return ""
        
        return Jsoup.clean(input, Safelist.simpleText())
            .trim()
    }

    fun sanitizeEmail(email: String?): String {
        if (email.isNullOrBlank()) return ""
        
        return email.trim()
            .replace(Regex("[<>\"'&]"), "")
            .lowercase()
    }

    fun sanitizePhone(phone: String?): String {
        if (phone.isNullOrBlank()) return ""
        
        return phone.trim()
            .replace(Regex("[^0-9+()\\-\\s]"), "")
    }

    fun sanitizeCpfCnpj(cpfCnpj: String?): String {
        if (cpfCnpj.isNullOrBlank()) return ""
        
        return cpfCnpj.trim()
            .replace(Regex("[^0-9]"), "")
    }

    fun sanitizeZipCode(zipCode: String?): String {
        if (zipCode.isNullOrBlank()) return ""
        
        return zipCode.trim()
            .replace(Regex("[^0-9\\-]"), "")
    }

    fun validateStringLength(input: String?, minLength: Int = 0, maxLength: Int = Int.MAX_VALUE): Boolean {
        if (input.isNullOrBlank()) return minLength == 0
        return input.length in minLength..maxLength
    }

    fun validateEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email.trim())
    }
}
