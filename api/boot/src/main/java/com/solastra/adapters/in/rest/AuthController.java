package com.solastra.adapters.in.rest;

import com.solastra.adapters.in.rest.model.ErrorResponseDto;
import com.solastra.adapters.in.rest.model.LoginRequest;
import com.solastra.adapters.in.rest.model.LoginResponseDto;
import com.solastra.adapters.in.rest.model.RegisterRequest;
import com.solastra.adapters.in.rest.model.RegisterResponseDto;
import com.solastra.application.port.in.AuthUseCase;
import com.solastra.application.port.in.model.LoginCommand;
import com.solastra.application.port.in.model.LoginResponse;
import com.solastra.application.port.in.model.RegisterCommand;
import com.solastra.application.port.in.model.RegisterResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequest request) {

        RegisterCommand command = new RegisterCommand(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.middleName(),
                request.prefix(),
                request.suffix(),
                request.username(),
                request.accountName()
        );

        RegisterResponse response = authUseCase.register(command);

        RegisterResponseDto dto = RegisterResponseDto.from(response);

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequest request) {
            LoginCommand command = new LoginCommand(
                    request.email(),
                    request.password()
            );

            LoginResponse response = authUseCase.login(command);

            LoginResponseDto dto = new LoginResponseDto(
                    true,
                    response.token(),
                    null
            );

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(dto);
    }
}