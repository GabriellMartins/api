package com.kodexerp.backend.shared.security

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class LoginRateLimitFilter : OncePerRequestFilter() {

    private val buckets = ConcurrentHashMap<String, Bucket>()
    
    private val bucketCapacity = 5
    private val refillTokens = 5
    private val refillDuration = Duration.ofMinutes(15)

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return !path.startsWith("/auth/login")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientIp = getClientIp(request)
        val bucket = resolveBucket(clientIp)

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response)
        } else {
            response.status = 429
            response.contentType = "application/json"
            response.writer.write(
                """{"success": false, "error": {"code": "TOO_MANY_LOGIN_ATTEMPTS", "message": "Too many login attempts. Please try again in 15 minutes."}}"""
            )
        }
    }

    private fun resolveBucket(clientIp: String): Bucket {
        return buckets.getOrPut(clientIp) {
            Bucket4j.builder()
                .addLimit(
                    Bandwidth.classic(bucketCapacity.toLong(), 
                        Refill.greedy(refillTokens.toLong(), refillDuration))
                )
                .build()
        }
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim()
        }
        val xRealIp = request.getHeader("X-Real-IP")
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp
        }
        return request.remoteAddr
    }
}
