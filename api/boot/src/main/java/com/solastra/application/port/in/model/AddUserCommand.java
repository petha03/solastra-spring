package com.solastra.application.port.in.model;

import com.solastra.domain.model.UserRole;

public record AddUserCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String middleName,
        String prefix,
        String suffix,
        String username,
        UserRole role,
        String accountId
) {}