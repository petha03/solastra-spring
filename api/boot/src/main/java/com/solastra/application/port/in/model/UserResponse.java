package com.solastra.application.port.in.model;

import com.solastra.domain.model.User;
import com.solastra.domain.model.UserRole;

public record UserResponse(
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
        java.time.Instant createdTimestamp
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getPrefix(),
                user.getSuffix(),
                user.getUsername(),
                user.getRole(),
                user.getAccountId(),
                user.getCreatedTimestamp()
        );
    }
}