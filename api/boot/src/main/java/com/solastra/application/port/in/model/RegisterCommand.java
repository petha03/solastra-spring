package com.solastra.application.port.in.model;

public record RegisterCommand(
        String email,
        String password,
        String name,
        String accountName
) {}