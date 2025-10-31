package com.solastra.application.port.in;

import com.solastra.application.port.in.model.LoginCommand;
import com.solastra.application.port.in.model.LoginResponse;
import com.solastra.application.port.in.model.RegisterCommand;
import com.solastra.application.port.in.model.RegisterResponse;

public interface AuthUseCase {
    /**
     * Authenticate a user and return a login response with JWT token
     *
     * @param command The login command
     * @return The login response containing the JWT token
     */
    LoginResponse login(LoginCommand command);

    /**
     * Register a new account with the first admin user
     *
     * @param command The registration command
     * @return The registration response with token and account
     */
    RegisterResponse register(RegisterCommand command);
}