package com.solastra.adapters.in.rest.model;

import jakarta.validation.constraints.NotBlank;

public record CreateBillingInfoRequest(
        @NotBlank(message = "Account ID is required")
        String accountId,

        @NotBlank(message = "Stripe customer ID is required")
        String stripeCustomerId
) {}