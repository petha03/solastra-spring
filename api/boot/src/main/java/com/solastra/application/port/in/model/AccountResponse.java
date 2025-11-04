package com.solastra.application.port.in.model;

import com.solastra.domain.model.Account;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record AccountResponse(
        String id,
        String name,
        Instant createdTimestamp,
        UserResponse owner,
        List<UserResponse> users
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getCreatedTimestamp(),
                account.getOwner() != null ? UserResponse.from(account.getOwner()) : null,
                account.getUsers() != null ? account.getUsers().stream()
                        .map(UserResponse::from)
                        .collect(Collectors.toList()) : List.of()
        );
    }
}