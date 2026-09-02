package com.Arjun.MyMarket.inventory.web;

import com.Arjun.MyMarket.inventory.exception.BusinessRuleException;
import com.Arjun.MyMarket.inventory.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handNotFound(ResourceNotFoundException ex, HttpServletRequest request){
       return build(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException ex, HttpServletRequest request){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
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

    public record ApiError(
            Instant timeStamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> validationErrors
    ){

    }

}
