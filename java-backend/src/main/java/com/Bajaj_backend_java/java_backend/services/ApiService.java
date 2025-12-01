package com.Bajaj_backend_java.java_backend.services;

import com.Bajaj_backend_java.java_backend.dto.SubmissionRequest;
import com.Bajaj_backend_java.java_backend.dto.WebhookRequest;
import com.Bajaj_backend_java.java_backend.dto.WebhookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private static final int MAX_RETRIES = 3;

    public ApiService(RestTemplate restTemplate, @Value("${api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public WebhookResponse generateWebhook(WebhookRequest request) {
        System.out.println("1. Requesting Webhook...");
        String url = baseUrl + "/generateWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

        return executeWithRetry(() ->
                restTemplate.postForObject(url, entity, WebhookResponse.class)
        );
    }

    public String submitSolution(String webhookUrl, String accessToken, String query) {
        // Enforce the URL from Step 4 instructions
        String targetUrl = baseUrl + "/testWebhook/JAVA";
        System.out.println("3. Submitting to: " + targetUrl);

        // DEBUG: Check if token is present
        if (accessToken == null || accessToken.isEmpty()) {
            System.err.println("CRITICAL ERROR: Access Token is NULL or Empty!");
        } else {
            System.out.println("   Token Length: " + accessToken.length());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        SubmissionRequest body = new SubmissionRequest(query);
        HttpEntity<SubmissionRequest> entity = new HttpEntity<>(body, headers);

        return executeWithRetry(() ->
                restTemplate.postForObject(targetUrl, entity, String.class)
        );
    }

    private <T> T executeWithRetry(java.util.function.Supplier<T> action) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                return action.get();
            } catch (RestClientException e) {
                attempts++;
                System.err.println("   Attempt " + attempts + " failed: " + e.getMessage());
                if (attempts >= MAX_RETRIES) {
                    throw new RuntimeException("API call failed after " + MAX_RETRIES + " attempts", e);
                }
                try {
                    Thread.sleep(1000 * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry backoff", ie);
                }
            }
        }
        return null;
    }
}