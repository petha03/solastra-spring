package com.solastra.application.service;

import com.solastra.application.port.in.AccountManagementUseCase;
import com.solastra.application.port.in.model.AccountResponse;
import com.solastra.application.port.in.model.GetAccountQuery;
import com.solastra.application.port.in.model.UpdateAccountCommand;
import com.solastra.application.port.out.AccountRepository;
import com.solastra.application.port.out.UserRepository;
import com.solastra.domain.model.Account;
import com.solastra.domain.model.User;
import com.solastra.domain.model.UserRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountManagementService implements AccountManagementUseCase {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountManagementService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccountResponse getAccount(GetAccountQuery query) {
        Account account = accountRepository.findById(query.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + query.accountId()));

        // Populate owner and users from the user repository
        populateAccountUsers(account);

        return AccountResponse.from(account);
    }

    @Override
    public AccountResponse updateAccount(UpdateAccountCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + command.accountId()));

        account.setName(command.name());
        Account updatedAccount = accountRepository.save(account);

        // Populate owner and users from the user repository
        populateAccountUsers(updatedAccount);

        return AccountResponse.from(updatedAccount);
    }

    private void populateAccountUsers(Account account) {
        List<User> allUsers = userRepository.findAllByAccountId(account.getId());

        // Owner is the PLANNER user
        User owner = allUsers.stream()
                .filter(user -> user.getRole() == UserRole.PLANNER)
                .findFirst()
                .orElse(null);

        // Users list excludes the PLANNER (owner)
        List<User> users = allUsers.stream()
                .filter(user -> user.getRole() != UserRole.PLANNER)
                .collect(Collectors.toList());

        account.setOwner(owner);
        account.setUsers(users);
    }
}