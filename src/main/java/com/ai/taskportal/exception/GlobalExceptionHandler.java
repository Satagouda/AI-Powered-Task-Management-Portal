package com.ai.taskportal.exception;

import com.ai.taskportal.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(
            TaskNotFoundException ex
    ) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex
    ) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex
    ) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex
    ) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message("Invalid email or password")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex
    ) {

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation failed");

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message(message)
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex
    ) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }
}