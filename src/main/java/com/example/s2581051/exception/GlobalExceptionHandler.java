package com.example.s2581051.exception;

import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Global exception handler for the application.
 * This class intercepts and handles exceptions globally across all controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles malformed JSON or invalid request body
     * @return HTTP 400 with the error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson() {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Malformed JSON or invalid field types",
                "status", 400
        ));
    }

    /**
     * Handles validation errors
     * @return HTTP 400 with the validation error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getFieldError() == null
                ? "Invalid input"
                : ex.getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(Map.of(
                "error", msg,
                "status", 400
        ));
    }
}
