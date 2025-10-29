package com.solastra.application.port.in;

import java.io.InputStream;

public interface UploadFileUseCase {

    /**
     * Upload a file to S3 storage
     *
     * @param fileName The original filename
     * @param contentType The MIME type
     * @param inputStream The file content
     * @param contentLength The file size
     * @return The S3 key where the file was stored
     */
    String uploadFile(String fileName, String contentType, InputStream inputStream, long contentLength);
}
