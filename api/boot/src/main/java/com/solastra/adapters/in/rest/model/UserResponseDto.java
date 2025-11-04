package com.solastra.adapters.in.rest.model;

import com.solastra.application.port.in.model.UserResponse;
import com.solastra.domain.model.UserRole;

import java.time.Instant;

public record UserResponseDto(
        String id,
        String email,
        String firstName,
        String lastName,
        String middleName,
        String prefix,
        String suffix,
        String username,
        UserRole role,
        String accountId,
        Instant createdTimestamp
) {
    public static UserResponseDto from(UserResponse userResponse) {
        return new UserResponseDto(
                userResponse.id(),
                userResponse.email(),
                userResponse.firstName(),
                userResponse.lastName(),
                userResponse.middleName(),
                userResponse.prefix(),
                userResponse.suffix(),
                userResponse.username(),
                userResponse.role(),
                userResponse.accountId(),
                userResponse.createdTimestamp()
        );
    }
}