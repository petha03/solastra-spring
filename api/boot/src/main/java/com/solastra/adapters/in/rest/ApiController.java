package com.solastra.adapters.in.rest;

import com.solastra.application.port.in.UploadFileUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class ApiController {

    private final UploadFileUseCase uploadFileUseCase;

    public ApiController(UploadFileUseCase uploadFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @PostMapping
    public String test(@RequestBody String body) {
        return "Echo: " + body;
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
}