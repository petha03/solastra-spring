package com.solastra.domain.model;

import java.time.Instant;

public class User {
    private String id;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String middleName;
    private String prefix;
    private String suffix;
    private String username;
    private UserRole role;
    private String accountId;
    private Instant createdTimestamp;

    public User() {
    }

    public User(String id, String email, String passwordHash, String firstName, String lastName, String middleName, String prefix, String suffix, String username, UserRole role, String accountId, Instant createdTimestamp) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.username = username;
        this.role = role;
        this.accountId = accountId;
        this.createdTimestamp = createdTimestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}