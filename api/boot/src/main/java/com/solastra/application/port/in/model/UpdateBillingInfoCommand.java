package com.solastra.application.port.in.model;

public record UpdateBillingInfoCommand(
        String accountId,
        String stripeCustomerId
) {}