package com.solastra.adapters.in.rest.model;

public record FileUploadResponseDto(
        String fileName,
        String contentType,
        Long size,
        String s3Key,
        String message
) {}