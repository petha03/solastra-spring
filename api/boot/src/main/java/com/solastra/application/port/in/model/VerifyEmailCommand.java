package com.solastra.application.port.in.model;

public record VerifyEmailCommand(
        String userId,
        String token
) {}