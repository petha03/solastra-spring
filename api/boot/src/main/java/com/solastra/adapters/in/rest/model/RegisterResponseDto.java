package com.solastra.adapters.in.rest.model;

import com.solastra.application.port.in.model.RegisterResponse;

public record RegisterResponseDto(
        String message,
        UserResponseDto owner
) {
    public static RegisterResponseDto from(RegisterResponse response) {
        return new RegisterResponseDto(
                response.message(),
                UserResponseDto.from(response.owner())
        );
    }
}