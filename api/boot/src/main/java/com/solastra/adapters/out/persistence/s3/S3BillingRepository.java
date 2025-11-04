package com.solastra.adapters.out.persistence.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solastra.application.port.out.BillingRepository;
import com.solastra.domain.model.BillingInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Repository
public class S3BillingRepository implements BillingRepository {

    private final S3Client s3Client;
    private final String rootBucketName;
    private final String billingBucketName;
    private final ObjectMapper objectMapper;

    public S3BillingRepository(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String rootBucketName) {
        this.s3Client = s3Client;
        this.rootBucketName = rootBucketName;
        this.billingBucketName = rootBucketName; // Use root bucket name from config
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
                    .bucket(billingBucketName)
                    .build());
        } catch (NoSuchBucketException e) {
            // Bucket doesn't exist, create it
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(billingBucketName)
                    .build());
        }
    }

    @Override
    public BillingInfo save(BillingInfo billingInfo) {
        try {
            // Save the billing info under accounts/{account_id}/billing/info.json
            String s3Key = buildBillingKey(billingInfo.getAccountId());
            String json = objectMapper.writeValueAsString(billingInfo);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(billingBucketName)
                    .key(s3Key)
                    .contentType("application/json")
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromString(json, StandardCharsets.UTF_8));

            // Save stripeCustomerId->accountId index
            String indexKey = buildStripeCustomerIndexKey(billingInfo.getStripeCustomerId());
            PutObjectRequest indexPutRequest = PutObjectRequest.builder()
                    .bucket(billingBucketName)
                    .key(indexKey)
                    .contentType("text/plain")
                    .build();

            s3Client.putObject(indexPutRequest, RequestBody.fromString(billingInfo.getAccountId(), StandardCharsets.UTF_8));

            return billingInfo;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save billing info to S3", e);
        }
    }

    @Override
    public Optional<BillingInfo> findByAccountId(String accountId) {
        try {
            String s3Key = buildBillingKey(accountId);

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(billingBucketName)
                    .key(s3Key)
                    .build();

            byte[] data = s3Client.getObject(getRequest).readAllBytes();
            BillingInfo billingInfo = objectMapper.readValue(data, BillingInfo.class);

            return Optional.of(billingInfo);
        } catch (NoSuchKeyException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read billing info from S3", e);
        }
    }

    @Override
    public Optional<BillingInfo> findByStripeCustomerId(String stripeCustomerId) {
        try {
            // Try to read the stripe customer index
            String indexKey = buildStripeCustomerIndexKey(stripeCustomerId);

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(billingBucketName)
                    .key(indexKey)
                    .build();

            byte[] data = s3Client.getObject(getRequest).readAllBytes();
            String accountId = new String(data, StandardCharsets.UTF_8);

            // Now fetch the actual billing info
            return findByAccountId(accountId);
        } catch (NoSuchKeyException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stripe customer index from S3", e);
        }
    }

    @Override
    public void deleteByAccountId(String accountId) {
        // First get the billing info to retrieve the stripe customer ID for index deletion
        Optional<BillingInfo> billingInfo = findByAccountId(accountId);

        // Delete the billing info object
        String s3Key = buildBillingKey(accountId);
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(billingBucketName)
                .key(s3Key)
                .build();
        s3Client.deleteObject(deleteRequest);

        // Delete the stripe customer index if billing info existed
        billingInfo.ifPresent(info -> {
            String indexKey = buildStripeCustomerIndexKey(info.getStripeCustomerId());
            DeleteObjectRequest indexDeleteRequest = DeleteObjectRequest.builder()
                    .bucket(billingBucketName)
                    .key(indexKey)
                    .build();
            s3Client.deleteObject(indexDeleteRequest);
        });
    }

    private String buildBillingKey(String accountId) {
        return String.format("accounts/%s/billing/info.json", accountId);
    }

    private String buildStripeCustomerIndexKey(String stripeCustomerId) {
        return String.format("billing/_index/stripe-customer/%s", stripeCustomerId);
    }
}