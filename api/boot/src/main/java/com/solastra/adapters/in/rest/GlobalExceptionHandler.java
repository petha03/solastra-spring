package com.solastra.adapters.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiController.ErrorResponseDto> handleValidationExceptions(
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

        ApiController.ErrorResponseDto errorDto = new ApiController.ErrorResponseDto(
                false,
                errorMessage
        );

        return ResponseEntity.badRequest()
                .header("Access-Control-Allow-Origin", "*")
                .body(errorDto);
    }
}