package com.solastra.adapters.out.persistence.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solastra.application.port.out.UserRepository;
import com.solastra.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Repository
public class S3UserRepository implements UserRepository {

    private final S3Client s3Client;
    private final String accountsBucketName;
    private final ObjectMapper objectMapper;

    public S3UserRepository(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String rootBucketName) {
        this.s3Client = s3Client;
        this.accountsBucketName = rootBucketName; // Use root bucket name from config
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // Register JavaTimeModule for Instant
    }

    @Override
    public User save(User user) {
        try {
            String s3Key = buildUserKey(user.getAccountId(), user.getId());
            String json = objectMapper.writeValueAsString(user);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(accountsBucketName)
                    .key(s3Key)
                    .contentType("application/json")
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromString(json, StandardCharsets.UTF_8));

            return user;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user to S3", e);
        }
    }

    @Override
    public Optional<User> findByAccountIdAndEmail(String accountId, String email) {
        try {
            // List all users in the account and find by email
            String prefix = buildUserPrefix(accountId);

            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(accountsBucketName)
                    .prefix(prefix)
                    .build();

            return s3Client.listObjectsV2(listRequest).contents().stream()
                    .map(S3Object::key)
                    .map(key -> getUserByKey(key))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by email from S3", e);
        }
    }

    @Override
    public Optional<User> findByAccountIdAndId(String accountId, String userId) {
        try {
            String s3Key = buildUserKey(accountId, userId);

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(accountsBucketName)
                    .key(s3Key)
                    .build();

            byte[] data = s3Client.getObject(getRequest).readAllBytes();
            User user = objectMapper.readValue(data, User.class);

            return Optional.of(user);
        } catch (NoSuchKeyException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read user from S3", e);
        }
    }

    @Override
    public boolean existsByAccountIdAndEmail(String accountId, String email) {
        return findByAccountIdAndEmail(accountId, email).isPresent();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // This implementation assumes the user is an account owner
        // For non-owner users, we would need a separate email->accountId index
        // For now, we search across all accounts (expensive, should be optimized)
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(accountsBucketName)
                    .build();

            return s3Client.listObjectsV2(listRequest).contents().stream()
                    .map(S3Object::key)
                    .filter(key -> key.endsWith(".json") && !key.contains("metadata"))
                    .map(this::getUserByKey)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(user -> user.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by email from S3", e);
        }
    }

    private Optional<User> getUserByKey(String key) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(accountsBucketName)
                    .key(key)
                    .build();

            byte[] data = s3Client.getObject(getRequest).readAllBytes();
            User user = objectMapper.readValue(data, User.class);

            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String buildUserKey(String accountId, String userId) {
        return String.format("accounts/%s/users/%s.json", accountId, userId);
    }

    private String buildUserPrefix(String accountId) {
        return String.format("accounts/%s/users/", accountId);
    }

    @Override
    public List<User> findAllByAccountId(String accountId) {
        try {
            String prefix = buildUserPrefix(accountId);

            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(accountsBucketName)
                    .prefix(prefix)
                    .build();

            return s3Client.listObjectsV2(listRequest).contents().stream()
                    .map(S3Object::key)
                    .filter(key -> key.endsWith(".json"))
                    .map(this::getUserByKey)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find users by accountId from S3", e);
        }
    }
}