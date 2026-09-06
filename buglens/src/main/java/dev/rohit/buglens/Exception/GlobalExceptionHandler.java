package dev.rohit.buglens.Exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException exception) {

        Map<String, String> response = Map.of(
                "error", "Invalid request",
                "message", exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(
            Exception exception) {

        Map<String, String> response = Map.of(
                "error", "Internal server error",
                "message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}