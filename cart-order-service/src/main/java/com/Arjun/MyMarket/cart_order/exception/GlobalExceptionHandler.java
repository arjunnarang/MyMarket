package com.Arjun.MyMarket.cart_order.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessRuleException ex, HttpServletRequest request){
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), request.getRequestURI(), null);

    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiError> handleExternalServiceException(ExternalServiceException ex, HttpServletRequest request){
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), request.getRequestURI(), null);
    }
    private ResponseEntity<ApiError> build(HttpStatus status, String message, String path, Map<String, String> validationErrors){
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                validationErrors
        ));
    }

    public record ApiError
        (Instant timeStamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors){
    }
}
