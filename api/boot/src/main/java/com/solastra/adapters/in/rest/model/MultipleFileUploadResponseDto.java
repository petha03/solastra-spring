package com.solastra.adapters.in.rest.model;

public record MultipleFileUploadResponseDto(
        Integer fileCount,
        String message
) {}