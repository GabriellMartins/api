package com.kodexerp.backend.auth.service

import com.kodexerp.backend.auth.dto.request.LoginRequest
import com.kodexerp.backend.auth.dto.request.RefreshRequest
import com.kodexerp.backend.auth.dto.request.RegisterRequest
import com.kodexerp.backend.auth.dto.response.AuthData
import com.kodexerp.backend.auth.dto.response.AuthResponse
import com.kodexerp.backend.shared.dto.response.MessageResponse
import com.kodexerp.backend.auth.dto.response.TokenData
import com.kodexerp.backend.auth.dto.response.TokenResponse
import com.kodexerp.backend.shared.dto.response.UserResponse
import com.kodexerp.backend.auth.entity.RefreshToken
import com.kodexerp.backend.auth.entity.User
import com.kodexerp.backend.auth.repository.RefreshTokenRepository
import com.kodexerp.backend.auth.repository.UserRepository
import com.kodexerp.backend.shared.security.JwtUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class   AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    @Value("\${jwt.refresh-expiration:604800000}")
    private lateinit var refreshExpiration: String

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already registered")
        }

        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            phone = request.phone,
            company = request.company
        )

        val savedUser = userRepository.save(user)
        val token = jwtUtil.generateToken(savedUser.id, savedUser.email, savedUser.role.name)
        val refreshToken = generateRefreshToken(savedUser.id)

        return AuthResponse(
            success = true,
            data = AuthData(
                user = mapToUserResponse(savedUser),
                token = token,
                refreshToken = refreshToken.token
            )
        )
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmailAndIsActive(request.email, true)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val updatedUser = user.copy(lastLogin = LocalDateTime.now())
        userRepository.save(updatedUser)

        val token = jwtUtil.generateToken(user.id, user.email, user.role.name)
        val refreshToken = generateRefreshToken(user.id)

        return AuthResponse(
            success = true,
            data = AuthData(
                user = mapToUserResponse(user),
                token = token,
                refreshToken = refreshToken.token
            )
        )
    }

    @Transactional
    fun logout(token: String): MessageResponse {
        val cleanToken = token.removePrefix("Bearer ").trim()
        val userId = jwtUtil.extractUserId(cleanToken)
        
        val refreshTokens = refreshTokenRepository.findByUserIdAndIsRevoked(userId, false)
        val updatedTokens = refreshTokens.map { it.copy(isRevoked = true) }
        refreshTokenRepository.saveAll(updatedTokens)

        return MessageResponse(
            success = true,
            message = "Logout realizado com sucesso"
        )
    }

    @Transactional
    fun refresh(request: RefreshRequest): TokenResponse {
        val refreshToken = refreshTokenRepository.findByToken(request.refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")

        if (refreshToken.isRevoked || refreshToken.expiresAt.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Refresh token expired or revoked")
        }

        val user = userRepository.findById(refreshToken.userId)
            .orElseThrow { throw IllegalArgumentException("User not found") }

        val updatedRefreshToken = refreshToken.copy(isRevoked = true)
        refreshTokenRepository.save(updatedRefreshToken)

        val newToken = jwtUtil.generateToken(user.id, user.email, user.role.name)
        val newRefreshToken = generateRefreshToken(user.id)

        return TokenResponse(
            success = true,
            data = TokenData(
                token = newToken,
                refreshToken = newRefreshToken.token
            )
        )
    }

    private fun generateRefreshToken(userId: UUID): RefreshToken {
        val tokenString = UUID.randomUUID().toString()
        val expiresAt = LocalDateTime.now().plusSeconds(refreshExpiration.toLong() / 1000)
        
        val refreshToken = RefreshToken(
            userId = userId,
            token = tokenString,
            expiresAt = expiresAt
        )
        
        return refreshTokenRepository.save(refreshToken)
    }

    private fun mapToUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            phone = user.phone,
            company = user.company,
            role = user.role,
            avatar = user.avatar,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}
