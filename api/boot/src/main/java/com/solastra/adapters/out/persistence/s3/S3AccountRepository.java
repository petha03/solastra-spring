package com.solastra.adapters.out.persistence.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solastra.application.port.out.AccountRepository;
import com.solastra.domain.model.Account;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Repository
public class S3AccountRepository implements AccountRepository {

    private final S3Client s3Client;
    private final String rootBucketName;
    private final String accountsBucketName;
    private final ObjectMapper objectMapper;

    public S3AccountRepository(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String rootBucketName) {
        this.s3Client = s3Client;
        this.rootBucketName = rootBucketName;
        this.accountsBucketName = rootBucketName; // Use root bucket name from config
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // Register JavaTimeModule for Instant
    }

    @PostConstruct
    public void init() {
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(accountsBucketName)
                    .build());
        } catch (NoSuchBucketException e) {
            // Bucket doesn't exist, create it
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(accountsBucketName)
                    .build());
        }
    }

    @Override
    public Account save(Account account) {
        try {
            // Save the account metadata
            String s3Key = buildAccountKey(account.getId());
            String json = objectMapper.writeValueAsString(account);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(accountsBucketName)
                    .key(s3Key)
                    .contentType("application/json")
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromString(json, StandardCharsets.UTF_8));

            // Save email->accountId index
            String indexKey = buildEmailIndexKey(account.getOwnerEmail());
            PutObjectRequest indexPutRequest = PutObjectRequest.builder()
                    .bucket(accountsBucketName)
                    .key(indexKey)
                    .contentType("text/plain")
                    .build();

            s3Client.putObject(indexPutRequest, RequestBody.fromString(account.getId(), StandardCharsets.UTF_8));

            return account;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save account to S3", e);
        }
    }

    @Override
    public Optional<Account> findById(String accountId) {
        try {
            String s3Key = buildAccountKey(accountId);

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(accountsBucketName)
                    .key(s3Key)
                    .build();

            byte[] data = s3Client.getObject(getRequest).readAllBytes();
            Account account = objectMapper.readValue(data, Account.class);

            return Optional.of(account);
        } catch (NoSuchKeyException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read account from S3", e);
        }
    }

    @Override
    public boolean existsByOwnerEmail(String email) {
        return findByOwnerEmail(email).isPresent();
    }

    @Override
    public Optional<Account> findByOwnerEmail(String email) {
        try {
            // Try to read the email index
            String indexKey = buildEmailIndexKey(email);

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(accountsBucketName)
                    .key(indexKey)
                    .build();

            byte[] data = s3Client.getObject(getRequest).readAllBytes();
            String accountId = new String(data, StandardCharsets.UTF_8);

            // Now fetch the actual account
            return findById(accountId);
        } catch (NoSuchKeyException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read email index from S3", e);
        }
    }

    private String buildAccountKey(String accountId) {
        return String.format("accounts/%s/metadata.json", accountId);
    }

    private String buildEmailIndexKey(String email) {
        // Use email as key in an index directory
        return String.format("accounts/_index/email/%s", email.toLowerCase());
    }
}