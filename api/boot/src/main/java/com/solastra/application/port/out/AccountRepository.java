package com.solastra.application.port.out;

import com.solastra.domain.model.Account;

import java.util.Optional;

public interface AccountRepository {
    /**
     * Save an account to storage
     *
     * @param account The account to save
     * @return The saved account
     */
    Account save(Account account);

    /**
     * Find an account by its ID
     *
     * @param accountId The account ID
     * @return Optional containing the account if found
     */
    Optional<Account> findById(String accountId);

    /**
     * Check if an account exists by owner email
     *
     * @param email The owner email
     * @return true if account exists
     */
    boolean existsByOwnerEmail(String email);

    /**
     * Find an account by owner email
     *
     * @param email The owner email
     * @return Optional containing the account if found
     */
    Optional<Account> findByOwnerEmail(String email);
}
