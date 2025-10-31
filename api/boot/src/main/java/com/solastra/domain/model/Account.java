package com.solastra.domain.model;

import java.time.Instant;

public class Account {
    private String id;
    private String name;
    private String ownerEmail;
    private Instant createdAt;

    public Account() {
    }

    public Account(String id, String name, String ownerEmail, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}