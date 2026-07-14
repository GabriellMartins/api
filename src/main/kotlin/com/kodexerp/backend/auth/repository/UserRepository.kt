package com.kodexerp.backend.auth.repository

import com.kodexerp.backend.auth.entity.User
import com.kodexerp.backend.auth.entity.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : MongoRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findByEmailAndIsActive(email: String, isActive: Boolean): User?
    fun existsByEmail(email: String): Boolean
    fun countByIsActiveTrue(): Long
    fun findByRoleAndIsActive(role: UserRole, isActive: Boolean, pageable: Pageable): Page<User>
}
