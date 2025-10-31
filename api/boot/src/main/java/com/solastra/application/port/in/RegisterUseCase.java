package com.solastra.application.port.in;

import com.solastra.domain.model.Account;

public interface RegisterUseCase {
    /**
     * Register a new account with the first admin user
     *
     * @param command The registration command
     * @return The registration response with token and account
     */
    RegisterResponse register(RegisterCommand command);

    record RegisterCommand(
            String email,
            String password,
            String name,
            String accountName
    ) {}

    record RegisterResponse(
            String token,
            Account account
    ) {}
}