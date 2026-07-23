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
class RateLimitFilter : OncePerRequestFilter() {

    private val buckets = ConcurrentHashMap<String, Bucket>()
    
    private val bucketCapacity = 100
    private val refillTokens = 100
    private val refillDuration = Duration.ofMinutes(1)

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
                """{"success": false, "error": {"code": "RATE_LIMIT_EXCEEDED", "message": "Too many requests. Please try again later."}}"""
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
