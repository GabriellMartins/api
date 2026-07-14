package com.kodexerp.backend.auth.controller

import com.kodexerp.backend.auth.dto.request.LoginRequest
import com.kodexerp.backend.auth.dto.request.RefreshRequest
import com.kodexerp.backend.auth.dto.request.RegisterRequest
import com.kodexerp.backend.auth.dto.response.AuthResponse
import com.kodexerp.backend.shared.dto.response.MessageResponse
import com.kodexerp.backend.auth.dto.response.TokenResponse
import com.kodexerp.backend.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val result = authService.register(request)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val result = authService.login(request)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") token: String): ResponseEntity<MessageResponse> {
        val result = authService.logout(token)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshRequest): ResponseEntity<TokenResponse> {
        val result = authService.refresh(request)
        return ResponseEntity.ok(result)
    }
}
