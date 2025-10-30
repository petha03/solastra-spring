package com.solastra;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Base class for integration tests.
 * Automatically fetches the API Gateway URL from Terraform when running tests.
 * Configures REST Assured with the base URL.
 */
@SpringBootTest(properties = {
    "spring.profiles.active=integrationTest"
})
public abstract class BaseIntegrationTest {

    @Value("${api.base.url:}")
    protected String apiBaseUrl;

    protected RequestSpecification requestSpec;

    @BeforeAll
    static void setupApiUrl() {
        // If api.base.url is not set as a system property, try to get it from Terraform
        if (System.getProperty("api.base.url") == null || System.getProperty("api.base.url").isEmpty()) {
            try {
                String projectRoot = System.getProperty("user.dir");
                if (projectRoot.endsWith("api/boot")) {
                    projectRoot = projectRoot.replace("/api/boot", "");
                }

                ProcessBuilder pb = new ProcessBuilder(
                    "terraform", "output", "-raw", "localstack_api_url"
                );
                pb.directory(new java.io.File(projectRoot + "/infra/terraform/localstack"));
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String url = reader.readLine();

                if (process.waitFor() == 0 && url != null && !url.isEmpty()) {
                    System.setProperty("api.base.url", url);
                    System.out.println("Using API Gateway URL from Terraform: " + url);
                }
            } catch (Exception e) {
                System.err.println("Could not get API Gateway URL from Terraform: " + e.getMessage());
                System.err.println("Using default URL");
            }
        }
    }

    @BeforeEach
    void setupRestAssured() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(apiBaseUrl)
                .build();

        RestAssured.baseURI = apiBaseUrl;
    }
}