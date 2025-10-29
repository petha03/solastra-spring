package com.solastra.adapters.out.persistence.s3;

import com.solastra.application.port.out.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Repository
public class S3FileRepository implements FileRepository {

    private final S3Client s3Client;
    private final String bucketName;

    public S3FileRepository(S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String saveFile(String fileName, String contentType, InputStream inputStream, long contentLength) {
        // Generate unique S3 key with timestamp and UUID to avoid collisions
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HHmmss"));
        String uniqueId = UUID.randomUUID().toString();
        String s3Key = String.format("uploads/%s/%s-%s", timestamp, uniqueId, fileName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));

        return s3Key;
    }
}
