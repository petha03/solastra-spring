package com.solastra.adapters.in.rest.model;

import java.time.Instant;

public record BillingInfoResponseDto(
        String id,
        String accountId,
        String stripeCustomerId,
        Instant createdTimestamp,
        Instant updatedTimestamp
) {}