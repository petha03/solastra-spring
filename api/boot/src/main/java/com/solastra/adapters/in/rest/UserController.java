package com.solastra.adapters.in.rest;

import com.solastra.adapters.in.rest.model.AddUserRequest;
import com.solastra.adapters.in.rest.model.EmailVerificationResponseDto;
import com.solastra.adapters.in.rest.model.UserResponseDto;
import com.solastra.application.port.in.UserManagementUseCase;
import com.solastra.application.port.in.model.AddUserCommand;
import com.solastra.application.port.in.model.EmailVerificationResponse;
import com.solastra.application.port.in.model.GetUserQuery;
import com.solastra.application.port.in.model.UserResponse;
import com.solastra.application.port.in.model.VerifyEmailCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserManagementUseCase userManagementUseCase;

    public UserController(UserManagementUseCase userManagementUseCase) {
        this.userManagementUseCase = userManagementUseCase;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(
            @PathVariable String userId,
            @RequestParam String accountId) {

        GetUserQuery query = new GetUserQuery(userId, accountId);
        UserResponse response = userManagementUseCase.getUser(query);

        UserResponseDto dto = new UserResponseDto(
                response.id(),
                response.email(),
                response.firstName(),
                response.lastName(),
                response.middleName(),
                response.prefix(),
                response.suffix(),
                response.username(),
                response.role(),
                response.accountId(),
                response.createdTimestamp()
        );

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dto);
    }

    @PostMapping("/{userId}/verify")
    public ResponseEntity<EmailVerificationResponseDto> verifyEmail(
            @PathVariable String userId,
            @RequestParam String token) {

        VerifyEmailCommand command = new VerifyEmailCommand(userId, token);
        EmailVerificationResponse response = userManagementUseCase.verifyEmail(command);

        EmailVerificationResponseDto dto = new EmailVerificationResponseDto(
                response.message(),
                response.login()
        );

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dto);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> addUser(
            @RequestParam String accountId,
            @Valid @RequestBody AddUserRequest request) {

        AddUserCommand command = new AddUserCommand(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.middleName(),
                request.prefix(),
                request.suffix(),
                request.username(),
                request.role(),
                accountId
        );

        UserResponse response = userManagementUseCase.addUser(command);
        UserResponseDto dto = UserResponseDto.from(response);

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dto);
    }
}