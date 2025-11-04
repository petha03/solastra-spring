package com.solastra.adapters.in.rest.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}