package com.solastra.adapters.in.rest.model;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountRequest(
        @NotBlank(message = "Account name is required")
        String name
) {}