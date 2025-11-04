package com.solastra.application.port.out;

import com.solastra.domain.model.BillingInfo;

import java.util.Optional;

public interface BillingRepository {
    /**
     * Save billing information
     *
     * @param billingInfo The billing info to save
     * @return The saved billing info
     */
    BillingInfo save(BillingInfo billingInfo);

    /**
     * Find billing info by account ID
     *
     * @param accountId The account ID
     * @return Optional containing the billing info if found
     */
    Optional<BillingInfo> findByAccountId(String accountId);

    /**
     * Find billing info by Stripe customer ID
     *
     * @param stripeCustomerId The Stripe customer ID
     * @return Optional containing the billing info if found
     */
    Optional<BillingInfo> findByStripeCustomerId(String stripeCustomerId);

    /**
     * Delete billing info by account ID
     *
     * @param accountId The account ID
     */
    void deleteByAccountId(String accountId);
}