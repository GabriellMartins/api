package com.kodexerp.backend.auth.repository

import com.kodexerp.backend.auth.entity.RefreshToken
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RefreshTokenRepository : MongoRepository<RefreshToken, UUID> {
    fun findByToken(token: String): RefreshToken?
    fun findByUserIdAndIsRevoked(userId: UUID, isRevoked: Boolean): List<RefreshToken>
    fun deleteByToken(token: String)
}
