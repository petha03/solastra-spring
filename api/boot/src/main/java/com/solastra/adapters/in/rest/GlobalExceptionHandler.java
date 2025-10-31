package com.solastra.adapters.in.rest;

import com.solastra.adapters.in.rest.model.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        // Collect all validation error messages
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));

        ErrorResponseDto errorDto = new ErrorResponseDto(
                false,
                errorMessage
        );

        return ResponseEntity.badRequest()
                .header("Access-Control-Allow-Origin", "*")
                .body(errorDto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        ErrorResponseDto errorDto = new ErrorResponseDto(
                false,
                ex.getMessage()
        );

        return ResponseEntity.badRequest()
                .header("Access-Control-Allow-Origin", "*")
                .body(errorDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex) {

        ErrorResponseDto errorDto = new ErrorResponseDto(
                false,
                ex.getMessage()
        );

        return ResponseEntity.status(500)
                .header("Access-Control-Allow-Origin", "*")
                .body(errorDto);
    }
}