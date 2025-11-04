package com.solastra.domain.model;

import java.time.Instant;

public class BillingInfo {
    private String id;
    private String accountId;
    private String stripeCustomerId;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;

    public BillingInfo() {
    }

    public BillingInfo(String id, String accountId, String stripeCustomerId, Instant createdTimestamp, Instant updatedTimestamp) {
        this.id = id;
        this.accountId = accountId;
        this.stripeCustomerId = stripeCustomerId;
        this.createdTimestamp = createdTimestamp;
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }
}