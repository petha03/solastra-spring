package com.solastra.adapters.in.rest.model;

import com.solastra.application.port.in.model.AccountResponse;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record AccountResponseDto(
        String id,
        String name,
        Instant createdTimestamp,
        UserResponseDto owner,
        List<UserResponseDto> users
) {
    public static AccountResponseDto from(AccountResponse accountResponse) {
        return new AccountResponseDto(
                accountResponse.id(),
                accountResponse.name(),
                accountResponse.createdTimestamp(),
                accountResponse.owner() != null ? UserResponseDto.from(accountResponse.owner()) : null,
                accountResponse.users() != null ? accountResponse.users().stream()
                        .map(UserResponseDto::from)
                        .collect(Collectors.toList()) : List.of()
        );
    }
}