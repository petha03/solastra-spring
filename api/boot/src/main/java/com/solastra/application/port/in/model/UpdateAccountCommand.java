package com.solastra.application.port.in.model;

public record UpdateAccountCommand(
        String accountId,
        String name
) {}