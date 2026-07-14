package com.kodexerp.backend.shared.exception

import com.kodexerp.backend.shared.dto.response.ErrorDetail
import com.kodexerp.backend.shared.dto.response.ErrorResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    success = false,
                    error = ErrorDetail(
                        code = "BAD_REQUEST",
                        message = ex.message ?: "Invalid request",
                        details = null
                    )
                )
            )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                ErrorResponse(
                    success = false,
                    error = ErrorDetail(
                        code = "UNAUTHORIZED",
                        message = "Invalid credentials",
                        details = null
                    )
                )
            )
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(ex: AuthenticationException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                ErrorResponse(
                    success = false,
                    error = ErrorDetail(
                        code = "UNAUTHORIZED",
                        message = "Authentication failed",
                        details = null
                    )
                )
            )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponse(
                    success = false,
                    error = ErrorDetail(
                        code = "FORBIDDEN",
                        message = "Access denied",
                        details = null
                    )
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(
                ErrorResponse(
                    success = false,
                    error = ErrorDetail(
                        code = "VALIDATION_ERROR",
                        message = "Validation failed",
                        details = errors
                    )
                )
            )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val errors = ex.constraintViolations.associate { 
            it.propertyPath.toString() to (it.message ?: "Invalid value") 
        }
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(
                ErrorResponse(
                    success = false,
                    error = ErrorDetail(
                        code = "VALIDATION_ERROR",
                        message = "Validation failed",
                        details = errors
                    )
                )
            )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    success = false,
                    error = ErrorDetail(
                        code = "BAD_REQUEST",
                        message = "Invalid parameter type: ${ex.name}",
                        details = null
                    )
                )
            )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    success = false,
                    error = ErrorDetail(
                        code = "NOT_FOUND",
                        message = ex.message ?: "Resource not found",
                        details = null
                    )
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    success = false,
                    error = ErrorDetail(
                        code = "INTERNAL_SERVER_ERROR",
                        message = "An unexpected error occurred",
                        details = null
                    )
                )
            )
    }
}
