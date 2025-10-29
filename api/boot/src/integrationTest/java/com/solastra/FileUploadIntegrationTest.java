package com.solastra;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Integration tests for file upload functionality.
 * These tests run against a deployed Lambda function in LocalStack.
 */
class FileUploadIntegrationTest extends BaseIntegrationTest {

    @Test
    void testFileUploadEndpoint() throws IOException {
        // Given
        File tempFile = File.createTempFile("test-file", ".txt");
        Files.writeString(tempFile.toPath(), "Test file content for integration test");
        tempFile.deleteOnExit();

        // When/Then
        given()
            .spec(requestSpec)
            .multiPart("file", tempFile, "text/plain")
        .when()
            .post("/upload/file")
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("fileName", equalTo(tempFile.getName()))
            .body("s3Key", notNullValue())
            .body("message", containsString("uploaded successfully"));
    }

    @Test
    void testHealthCheck() {
        // This is a basic connectivity test to ensure LocalStack is accessible
        String healthUrl = System.getProperty("aws.endpoint", "http://localhost:4566");

        given()
            .baseUri(healthUrl)
        .when()
            .get("/_localstack/health")
        .then()
            .statusCode(200);
    }
}