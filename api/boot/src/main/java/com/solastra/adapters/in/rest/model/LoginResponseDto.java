package com.solastra.adapters.in.rest.model;

public record LoginResponseDto(
        boolean success,
        String token,
        String error
) {}