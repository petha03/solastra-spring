package com.solastra.adapters.in.rest.model;

public record ErrorResponseDto(
        boolean success,
        String error
) {}