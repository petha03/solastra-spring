package com.solastra.application.port.out;

import com.solastra.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    /**
     * Save a user to storage
     *
     * @param user The user to save
     * @return The saved user
     */
    User save(User user);

    /**
     * Find a user by email within an account
     *
     * @param accountId The account ID
     * @param email The user email
     * @return Optional containing the user if found
     */
    Optional<User> findByAccountIdAndEmail(String accountId, String email);

    /**
     * Find a user by ID within an account
     *
     * @param accountId The account ID
     * @param userId The user ID
     * @return Optional containing the user if found
     */
    Optional<User> findByAccountIdAndId(String accountId, String userId);

    /**
     * Check if a user exists by email within an account
     *
     * @param accountId The account ID
     * @param email The user email
     * @return true if user exists
     */
    boolean existsByAccountIdAndEmail(String accountId, String email);

    /**
     * Find a user by email across all accounts
     * This is used for login where we don't know the account ID yet
     *
     * @param email The user email
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all users for a given account
     *
     * @param accountId The account ID
     * @return List of users in the account
     */
    java.util.List<User> findAllByAccountId(String accountId);
}