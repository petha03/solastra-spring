package com.solastra.adapters.in.rest;

import com.solastra.application.port.in.LoginUseCase;
import com.solastra.application.port.in.RegisterUseCase;
import com.solastra.application.port.in.UploadFileUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    private final UploadFileUseCase uploadFileUseCase;
    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;

    public ApiController(UploadFileUseCase uploadFileUseCase,
                        RegisterUseCase registerUseCase,
                        LoginUseCase loginUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/upload")
    public String test(@RequestBody String body) {
        return "Echo: " + body;
    }

    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            // Upload file to S3 via use case
            String s3Key = uploadFileUseCase.uploadFile(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getInputStream(),
                    file.getSize()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileName", file.getOriginalFilename());
            response.put("contentType", file.getContentType());
            response.put("size", file.getSize());
            response.put("s3Key", s3Key);
            response.put("message", "File uploaded successfully to S3");

            return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .body(response);
        } catch (Exception error) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", error.getMessage());
            return ResponseEntity.status(500)
                .header("Access-Control-Allow-Origin", "*")
                .body(errorResponse);
        }
    }

    @PostMapping(value = "/upload/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(@RequestPart("files") List<MultipartFile> files) {
        try {
            if (files.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "No files provided");
                return ResponseEntity.badRequest()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(errorResponse);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileCount", files.size());
            response.put("message", files.size() + " file(s) uploaded successfully");

            return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .body(response);
        } catch (Exception error) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", error.getMessage());
            return ResponseEntity.status(500)
                .header("Access-Control-Allow-Origin", "*")
                .body(errorResponse);
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterUseCase.RegisterCommand command = new RegisterUseCase.RegisterCommand(
                    request.email(),
                    request.password(),
                    request.name(),
                    request.accountName()
            );

            RegisterUseCase.RegisterResponse response = registerUseCase.register(command);

            RegisterResponseDto dto = new RegisterResponseDto(
                    true,
                    response.token(),
                    new AccountDto(
                            response.account().getId(),
                            response.account().getName(),
                            response.account().getOwnerEmail(),
                            response.account().getCreatedAt()
                    ),
                    "Account created successfully",
                    null
            );

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(dto);
        } catch (Exception e) {
            RegisterResponseDto errorDto = new RegisterResponseDto(
                    false,
                    null,
                    null,
                    null,
                    e.getMessage()
            );
            return ResponseEntity.badRequest()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(errorDto);
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginUseCase.LoginCommand command = new LoginUseCase.LoginCommand(
                    request.email(),
                    request.password()
            );

            String token = loginUseCase.login(command);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(401)
                    .header("Access-Control-Allow-Origin", "*")
                    .body(errorResponse);
        }
    }

    // Request DTOs
    record RegisterRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email format")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 8, message = "Password must be at least 8 characters")
            String password,

            @NotBlank(message = "Name is required")
            String name,

            @NotBlank(message = "Account name is required")
            String accountName
    ) {}

    record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email format")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {}

    // Response DTOs
    record RegisterResponseDto(
            boolean success,
            String token,
            AccountDto account,
            String message,
            String error
    ) {}

    record AccountDto(
            String id,
            String name,
            String ownerEmail,
            java.time.Instant createdAt
    ) {}

    record ErrorResponseDto(
            boolean success,
            String error
    ) {}
}