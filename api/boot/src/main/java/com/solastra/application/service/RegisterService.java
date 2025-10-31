package com.solastra.application.service;

import com.solastra.application.port.in.RegisterUseCase;
import com.solastra.application.port.out.AccountRepository;
import com.solastra.application.port.out.UserRepository;
import com.solastra.domain.model.Account;
import com.solastra.domain.model.User;
import com.solastra.infrastructure.security.JwtTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RegisterService implements RegisterUseCase {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public RegisterService(
            AccountRepository accountRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public RegisterResponse register(RegisterCommand command) {
        // Check if account with this owner email already exists
        if (accountRepository.existsByOwnerEmail(command.email())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        // Create new account
        String accountId = UUID.randomUUID().toString();
        Account account = new Account(
                accountId,
                command.accountName(),
                command.email(),
                Instant.now()
        );
        accountRepository.save(account);

        // Create admin user for the account
        String userId = UUID.randomUUID().toString();
        String passwordHash = passwordEncoder.encode(command.password());

        User user = new User(
                userId,
                command.email(),
                passwordHash,
                command.name(),
                "admin", // First user is always admin
                accountId,
                Instant.now()
        );
        userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenService.generateToken(
                userId,
                command.email(),
                accountId,
                "admin"
        );

        return new RegisterResponse(token, account);
    }
}