package com.solastra.application.port.in;

public interface LoginUseCase {
    /**
     * Authenticate a user and return a JWT token
     *
     * @param command The login command
     * @return The JWT token
     */
    String login(LoginCommand command);

    record LoginCommand(
            String email,
            String password
    ) {}
}