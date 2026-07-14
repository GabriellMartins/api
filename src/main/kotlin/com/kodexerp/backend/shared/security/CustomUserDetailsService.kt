package com.kodexerp.backend.shared.security

import com.kodexerp.backend.auth.entity.User
import com.kodexerp.backend.auth.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmailAndIsActive(email, true)
            ?: throw UsernameNotFoundException("User not found with email: $email")

        return CustomUserDetails(user)
    }
}

data class CustomUserDetails(
    private val user: User
) : UserDetails {
    override fun getAuthorities() = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
    override fun getPassword() = user.password
    override fun getUsername() = user.email
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = user.isActive
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = user.isActive
}
