package com.solastra.application.port.in.model;

import com.solastra.domain.model.BillingInfo;

import java.time.Instant;

public record BillingInfoResponse(
        String id,
        String accountId,
        String stripeCustomerId,
        Instant createdTimestamp,
        Instant updatedTimestamp
) {
    public static BillingInfoResponse from(BillingInfo billingInfo) {
        return new BillingInfoResponse(
                billingInfo.getId(),
                billingInfo.getAccountId(),
                billingInfo.getStripeCustomerId(),
                billingInfo.getCreatedTimestamp(),
                billingInfo.getUpdatedTimestamp()
        );
    }
}