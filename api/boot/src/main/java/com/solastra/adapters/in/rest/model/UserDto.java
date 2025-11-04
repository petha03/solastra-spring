package com.solastra.adapters.in.rest.model;

import com.solastra.domain.model.User;
import com.solastra.domain.model.UserRole;

import java.time.Instant;

public record UserDto(
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
    public static UserDto from(User user) {
        return new UserDto(
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