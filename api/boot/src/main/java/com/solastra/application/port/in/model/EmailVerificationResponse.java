package com.solastra.application.port.in.model;

public record EmailVerificationResponse(
        String message,
        String login
) {}