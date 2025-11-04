package com.solastra.application.service;

import com.solastra.application.port.in.AuthUseCase;
import com.solastra.application.port.in.model.*;
import com.solastra.application.port.out.AccountRepository;
import com.solastra.application.port.out.UserRepository;
import com.solastra.domain.model.Account;
import com.solastra.domain.model.User;
import com.solastra.domain.model.UserRole;
import com.solastra.infrastructure.security.JwtTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService implements AuthUseCase {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(
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
    public LoginResponse login(LoginCommand command) {
        // Find user by email
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtTokenService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getAccountId(),
                user.getRole()
        );

        return new LoginResponse(token);
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
                command.firstName(),
                command.lastName(),
                command.middleName(),
                command.prefix(),
                command.suffix(),
                command.username(),
                UserRole.PLANNER, // First user defaults to PLANNER
                accountId,
                Instant.now()
        );
        userRepository.save(user);

        UserResponse owner = UserResponse.from(user);
        return new RegisterResponse(
                "Account created successfully",
                owner
        );
    }
}