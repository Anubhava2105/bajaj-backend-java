package com.Bajaj_backend_java.java_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebhookRequest {
    private String name;
    private String regNo;
    private String email;
}