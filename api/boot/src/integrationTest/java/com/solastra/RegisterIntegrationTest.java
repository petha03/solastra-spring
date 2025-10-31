package com.solastra;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for user registration endpoint
 */
public class RegisterIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldRegisterNewAccount() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "test@example.com");
        requestBody.put("password", "password123");
        requestBody.put("name", "Test User");
        requestBody.put("accountName", "Test Account");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("token", notNullValue())
            .body("token", not(emptyString()))
            .body("account", notNullValue())
            .body("account.id", notNullValue())
            .body("account.name", equalTo("Test Account"))
            .body("account.ownerEmail", equalTo("test@example.com"))
            .body("account.createdAt", notNullValue())
            .body("message", equalTo("Account created successfully"))
            .body("error", nullValue());
    }

    @Test
    void shouldRejectRegistrationWithMissingEmail() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("password", "password123");
        requestBody.put("name", "Test User");
        requestBody.put("accountName", "Test Account");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("Email"));
    }

    @Test
    void shouldRejectRegistrationWithInvalidEmail() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "invalid-email");
        requestBody.put("password", "password123");
        requestBody.put("name", "Test User");
        requestBody.put("accountName", "Test Account");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("email"));
    }

    @Test
    void shouldRejectRegistrationWithShortPassword() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "test@example.com");
        requestBody.put("password", "short");
        requestBody.put("name", "Test User");
        requestBody.put("accountName", "Test Account");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("8 characters"));
    }

    @Test
    void shouldRejectRegistrationWithMissingName() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "test@example.com");
        requestBody.put("password", "password123");
        requestBody.put("accountName", "Test Account");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("Name"));
    }

    @Test
    void shouldRejectRegistrationWithMissingAccountName() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "test@example.com");
        requestBody.put("password", "password123");
        requestBody.put("name", "Test User");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("Account name"));
    }

    @Test
    void shouldRejectDuplicateEmailRegistration() {
        String email = "duplicate@example.com";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", "password123");
        requestBody.put("name", "Test User");
        requestBody.put("accountName", "Test Account");

        // First registration should succeed
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(200)
            .body("success", equalTo(true));

        // Second registration with same email should fail
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("already exists"));
    }

    @Test
    void shouldReturnValidJwtTokenOnSuccessfulRegistration() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "jwt-test@example.com");
        requestBody.put("password", "password123");
        requestBody.put("name", "JWT Test User");
        requestBody.put("accountName", "JWT Test Account");

        String token = given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("token", notNullValue())
            .extract()
            .path("token");

        // Verify token has 3 parts (header.payload.signature)
        String[] tokenParts = token.split("\\.");
        assert tokenParts.length == 3 : "JWT token should have 3 parts";
    }
}