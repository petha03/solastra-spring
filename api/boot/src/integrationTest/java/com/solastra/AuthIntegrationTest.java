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
public class AuthIntegrationTest extends BaseIntegrationTest {

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
            .body("message", equalTo("Account created successfully"));
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
            .body("message", equalTo("Account created successfully"));

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
    void shouldLoginAndReturnValidJwtToken() {
        // First register an account
        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("email", "login-test@example.com");
        registerBody.put("password", "password123");
        registerBody.put("name", "Login Test User");
        registerBody.put("accountName", "Login Test Account");

        given()
            .contentType(ContentType.JSON)
            .body(registerBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(200);

        // Now login with the same credentials
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", "login-test@example.com");
        loginBody.put("password", "password123");

        String token = given()
            .contentType(ContentType.JSON)
            .body(loginBody)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("token", notNullValue())
            .body("error", nullValue())
            .extract()
            .path("token");

        // Verify token has 3 parts (header.payload.signature)
        String[] tokenParts = token.split("\\.");
        assert tokenParts.length == 3 : "JWT token should have 3 parts";
    }
}