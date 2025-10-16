package com.example.s2581051.exception;

import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson() {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Malformed JSON or invalid field types",
                "status", 400
        ));
    }

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
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", ex.getMessage() == null ? "Unexpected error" : ex.getMessage(),
                "status", 400
        ));
    }
}
