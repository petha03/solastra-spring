package com.solastra.application.port.in.model;

public record LoginCommand(
        String email,
        String password
) {}
