package com.solastra.adapters.in.rest;

import com.solastra.adapters.in.rest.model.FileUploadResponseDto;
import com.solastra.adapters.in.rest.model.MultipleFileUploadResponseDto;
import com.solastra.application.port.in.UploadFileUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;

    public FileController(UploadFileUseCase uploadFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @PostMapping("/upload")
    public String test(@RequestBody String body) {
        return "Echo: " + body;
    }

    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponseDto> uploadFile(@RequestPart("file") MultipartFile file) throws Exception {
        // Upload file to S3 via use case
        String s3Key = uploadFileUseCase.uploadFile(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getInputStream(),
                file.getSize()
        );

        FileUploadResponseDto response = new FileUploadResponseDto(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                s3Key,
                "File uploaded successfully to S3"
        );

        return ResponseEntity.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
            .body(response);
    }

    @PostMapping(value = "/upload/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MultipleFileUploadResponseDto> uploadMultipleFiles(@RequestPart("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new IllegalArgumentException("No files provided");
        }

        MultipleFileUploadResponseDto response = new MultipleFileUploadResponseDto(
                files.size(),
                files.size() + " file(s) uploaded successfully"
        );

        return ResponseEntity.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
            .body(response);
    }
}