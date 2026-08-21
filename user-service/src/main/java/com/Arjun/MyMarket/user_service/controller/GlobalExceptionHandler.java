package com.Arjun.MyMarket.user_service.controller;

import com.Arjun.MyMarket.user_service.dto.ErrorResponse;
import com.Arjun.MyMarket.user_service.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ErrorResponse> handleNotfound(ResourceNotFoundException ex, HttpServletRequest request){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), List.of());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path, List<String> errorDetails){
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                errorDetails
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}
