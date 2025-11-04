package com.solastra.adapters.in.rest.model;

import jakarta.validation.constraints.NotBlank;

public record UpdateBillingInfoRequest(
        @NotBlank(message = "Stripe customer ID is required")
        String stripeCustomerId
) {}