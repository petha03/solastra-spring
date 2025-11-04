package com.solastra.application.port.in.model;

public record GetUserQuery(
        String userId,
        String accountId
) {}