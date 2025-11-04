package com.solastra.adapters.in.rest.model;

public record EmailVerificationResponseDto(
        String message,
        String loginPath
) {}