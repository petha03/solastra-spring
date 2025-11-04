package com.solastra.domain.model;

import java.time.Instant;

public class EmailVerificationToken {
    private String id;
    private String userId;
    private String token;
    private Instant expiresAt;
    private Instant createdAt;
    private boolean verified;

    public EmailVerificationToken() {
    }

    public EmailVerificationToken(String id, String userId, String token, Instant expiresAt, Instant createdAt, boolean verified) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.verified = verified;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !verified && !isExpired();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}