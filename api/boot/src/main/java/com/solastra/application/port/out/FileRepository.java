package com.solastra.application.port.out;

import java.io.InputStream;

public interface FileRepository {

    /**
     * Save a file to S3 storage
     *
     * @param fileName The name of the file
     * @param contentType The MIME type of the file
     * @param inputStream The file content as an InputStream
     * @param contentLength The size of the file in bytes
     * @return The S3 key/path where the file was saved
     */
    String saveFile(String fileName, String contentType, InputStream inputStream, long contentLength);
}
