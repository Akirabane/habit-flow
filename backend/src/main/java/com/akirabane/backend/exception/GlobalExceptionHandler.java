package com.akirabane.backend.exception;

import com.akirabane.backend.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiErrorDto buildError(HttpStatus status, String message, String path) {
        return new ApiErrorDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDto> handleResponseStatusException(ResponseStatusException ex,
                                                                     HttpServletRequest request) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        ApiErrorDto error = buildError(status, ex.getReason(), request.getRequestURI());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidationException(MethodArgumentNotValidException ex,
                                                                 HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiErrorDto error = buildError(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiErrorDto> handleErrorResponseException(ErrorResponseException ex,
                                                                    HttpServletRequest request) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        ApiErrorDto error = buildError(status, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericException(Exception ex,
                                                              HttpServletRequest request) {
        ApiErrorDto error = buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error",
                request.getRequestURI());
        ex.printStackTrace(); // log pour le dev
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}