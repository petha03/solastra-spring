package com.solastra;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for account management endpoints
 */
public class AccountManagementIntegrationTest extends BaseIntegrationTest {

    /**
     * Helper method to register an account and return the account ID
     */
    private String registerAccountAndGetId(String email, String accountName) {
        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("email", email);
        registerBody.put("password", "password123");
        registerBody.put("firstName", "Test");
        registerBody.put("lastName", "User");
        registerBody.put("accountName", accountName);

        given()
            .contentType(ContentType.JSON)
            .body(registerBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(200);

        // Login to get the account ID from the token or fetch it another way
        // For now, we'll need to derive the account ID
        // Since we don't have a direct way to get it from registration,
        // we'll use a test helper or assume we can query by email

        return null; // This will need to be implemented based on available APIs
    }

    @Test
    void shouldGetAccountById() {
        // First, register an account to have an account ID to test with
        String email = "get-account-test-" + UUID.randomUUID() + "@example.com";
        String accountName = "Get Account Test";
        String firstName = "Test";
        String lastName = "User";

        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("email", email);
        registerBody.put("password", "password123");
        registerBody.put("firstName", firstName);
        registerBody.put("lastName", lastName);
        registerBody.put("accountName", accountName);

        String accountId = given()
            .contentType(ContentType.JSON)
            .body(registerBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(200)
            .extract()
            .path("owner.accountId");

        // Now test the GET endpoint
        given()
            .when()
                .get("/accounts/{accountId}", accountId)
            .then()
                .statusCode(200)
                .body("id", equalTo(accountId))
                .body("name", equalTo(accountName))
                .body("owner", notNullValue())
                .body("owner.email", equalTo(email))
                .body("owner.firstName", equalTo(firstName))
                .body("owner.lastName", equalTo(lastName))
                .body("owner.role", equalTo("PLANNER"))
                .body("owner.accountId", equalTo(accountId))
                .body("users", hasSize(0))  // users list should not include the owner
                .body("createdTimestamp", notNullValue());
    }

    @Test
    void shouldUpdateAccountName() {
        // First register an account
        String email = "update-account-test-" + UUID.randomUUID() + "@example.com";
        String originalName = "Original Account Name";
        String firstName = "Test";
        String lastName = "User";

        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("email", email);
        registerBody.put("password", "password123");
        registerBody.put("firstName", firstName);
        registerBody.put("lastName", lastName);
        registerBody.put("accountName", originalName);

        String accountId = given()
            .contentType(ContentType.JSON)
            .body(registerBody)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(200)
            .extract()
            .path("owner.accountId");

        // Update the account name
        String newName = "Updated Account Name";
        Map<String, String> updateBody = new HashMap<>();
        updateBody.put("name", newName);

        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
        .when()
            .put("/accounts/{accountId}", accountId)
        .then()
            .statusCode(200)
            .body("id", equalTo(accountId))
            .body("name", equalTo(newName))
            .body("owner", notNullValue())
            .body("owner.email", equalTo(email))
            .body("owner.firstName", equalTo(firstName))
            .body("owner.lastName", equalTo(lastName))
            .body("owner.role", equalTo("PLANNER"))
            .body("users", hasSize(0))  // users list should not include the owner
            .body("createdTimestamp", notNullValue());
    }

    @Test
    void shouldRejectUpdateWithBlankName() {
        // Assuming we have a valid account ID
        String accountId = "test-account-id";

        Map<String, String> updateBody = new HashMap<>();
        updateBody.put("name", "");

        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
        .when()
            .put("/accounts/{accountId}", accountId)
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("name"));
    }

    @Test
    void shouldRejectUpdateWithMissingName() {
        // Assuming we have a valid account ID
        String accountId = "test-account-id";

        Map<String, String> updateBody = new HashMap<>();
        // No name field

        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
        .when()
            .put("/accounts/{accountId}", accountId)
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("name"));
    }

    @Test
    void shouldReturn400ForNonExistentAccount() {
        String nonExistentAccountId = "non-existent-account-id";

        given()
        .when()
            .get("/accounts/{accountId}", nonExistentAccountId)
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("not found"));
    }

    @Test
    void shouldReturn400WhenUpdatingNonExistentAccount() {
        String nonExistentAccountId = "non-existent-account-id";

        Map<String, String> updateBody = new HashMap<>();
        updateBody.put("name", "New Name");

        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
        .when()
            .put("/accounts/{accountId}", nonExistentAccountId)
        .then()
            .statusCode(400)
            .body("success", equalTo(false))
            .body("error", containsString("not found"));
    }
}