package com.Bajaj_backend_java.java_backend.runner;

import com.Bajaj_backend_java.java_backend.dto.WebhookRequest;
import com.Bajaj_backend_java.java_backend.dto.WebhookResponse;
import com.Bajaj_backend_java.java_backend.services.ApiService;
import com.Bajaj_backend_java.java_backend.services.SqlSolverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final ApiService apiService;
    private final SqlSolverService sqlService;

    @Value("${user.name}") private String name;
    @Value("${user.reg-no}") private String regNo;
    @Value("${user.email}") private String email;

    public StartupRunner(ApiService apiService, SqlSolverService sqlService) {
        this.apiService = apiService;
        this.sqlService = sqlService;
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("--- Starting Webhook Solver (MVC) ---");

            // 1. Generate Webhook
            WebhookRequest webhookReq = WebhookRequest.builder()
                    .name(name).regNo(regNo).email(email).build();

            WebhookResponse response = apiService.generateWebhook(webhookReq);

            if (response != null) {
                System.out.println("   Received Webhook: " + response.getWebhook());

                // 2. Solve SQL
                System.out.println("2. Solving for RegNo: " + regNo);
                String sqlQuery = sqlService.solveProblem(regNo);

                // 3. Submit
                String result = apiService.submitSolution(response.getWebhook(), response.getAccessToken(), sqlQuery);
                System.out.println("   Submission Response: " + result);
            }

            System.out.println("--- Finished ---");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}