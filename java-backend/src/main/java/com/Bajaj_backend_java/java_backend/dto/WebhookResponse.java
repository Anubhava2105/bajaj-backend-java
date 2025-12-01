package com.Bajaj_backend_java.java_backend.dto;

import lombok.Data;

@Data
public class WebhookResponse {
    private String webhook;
    private String accessToken;
}