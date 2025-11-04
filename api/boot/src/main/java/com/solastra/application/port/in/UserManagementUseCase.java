package com.solastra.application.port.in;

import com.solastra.application.port.in.model.AddUserCommand;
import com.solastra.application.port.in.model.EmailVerificationResponse;
import com.solastra.application.port.in.model.GetUserQuery;
import com.solastra.application.port.in.model.UserResponse;
import com.solastra.application.port.in.model.VerifyEmailCommand;

public interface UserManagementUseCase {
    /**
     * Get user details by ID
     *
     * @param query The query containing user ID and account ID
     * @return The user response
     */
    UserResponse getUser(GetUserQuery query);

    /**
     * Verify if an email exists in the system
     *
     * @param command The verification command containing email
     * @return The verification response
     */
    EmailVerificationResponse verifyEmail(VerifyEmailCommand command);

    /**
     * Add a new user to an account
     *
     * @param command The command containing user details
     * @return The created user response
     */
    UserResponse addUser(AddUserCommand command);
}