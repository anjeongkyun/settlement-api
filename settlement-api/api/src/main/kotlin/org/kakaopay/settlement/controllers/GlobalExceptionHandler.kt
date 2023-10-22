package org.kakaopay.settlement.controllers

import org.kakaopay.settlement.exceptions.ErrorResponse
import org.kakaopay.settlement.exceptions.HttpException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(HttpException::class)
    fun handleHttpException(exception: HttpException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = exception.status,
                className = exception.className,
                message = exception.message,
                errorProperties = exception.errorProperties
            ),
            HttpStatus.valueOf(exception.status)
        )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        exception.printStackTrace()
        return ResponseEntity(
            ErrorResponse(
                status = 500,
                className = "RuntimeException",
                message = "Unknown Internal Service"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
