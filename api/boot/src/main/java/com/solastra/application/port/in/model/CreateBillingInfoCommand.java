package com.solastra.application.port.in.model;

public record CreateBillingInfoCommand(
        String accountId,
        String stripeCustomerId
) {}