package com.solastra.application.service;

import com.solastra.application.port.in.UserManagementUseCase;
import com.solastra.application.port.in.model.AddUserCommand;
import com.solastra.application.port.in.model.EmailVerificationResponse;
import com.solastra.application.port.in.model.GetUserQuery;
import com.solastra.application.port.in.model.UserResponse;
import com.solastra.application.port.in.model.VerifyEmailCommand;
import com.solastra.application.port.out.UserRepository;
import com.solastra.domain.model.User;
import com.solastra.domain.model.UserRole;
import com.solastra.domain.service.TokenVerificationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserManagementService implements UserManagementUseCase {

    private final UserRepository userRepository;
    private final TokenVerificationService tokenVerificationService;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserRepository userRepository, TokenVerificationService tokenVerificationService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenVerificationService = tokenVerificationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse getUser(GetUserQuery query) {
        User user = userRepository.findByAccountIdAndId(query.accountId(), query.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + query.userId() + " in account: " + query.accountId()));

        return UserResponse.from(user);
    }

    @Override
    public EmailVerificationResponse verifyEmail(VerifyEmailCommand command) {
        boolean isValid = tokenVerificationService.verifyToken(
                command.userId(),
                command.token(),
                "email_verification"
        );

        if (isValid) {
            return new EmailVerificationResponse(
                    "Email verification successful",
                    "/auth/login"
            );
        } else {
            return new EmailVerificationResponse(
                    "Email verification failed",
                    null
            );
        }
    }

    @Override
    public UserResponse addUser(AddUserCommand command) {
        // Validate that role is not PLANNER
        if (command.role() == UserRole.PLANNER) {
            throw new IllegalArgumentException("Cannot add user with PLANNER role");
        }

        // Check if user with this email already exists
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Create new user
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
                command.role(),
                command.accountId(),
                Instant.now()
        );
        userRepository.save(user);

        return UserResponse.from(user);
    }
}