package com.solastra.domain.model;

import java.time.Instant;
import java.util.List;

public class Account {
    private String id;
    private String name;
    private String ownerEmail;
    private Instant createdTimestamp;
    private User owner;
    private List<User> users;

    public Account() {
    }

    public Account(String id, String name, String ownerEmail, Instant createdTimestamp) {
        this.id = id;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.createdTimestamp = createdTimestamp;
    }

    public Account(String id, String name, String ownerEmail, Instant createdTimestamp, User owner, List<User> users) {
        this.id = id;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.createdTimestamp = createdTimestamp;
        this.owner = owner;
        this.users = users;
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

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}