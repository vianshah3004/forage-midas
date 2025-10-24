package com.jpmc.midascore.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.jpmc.midascore.foundation.Transaction;

@Service
public class IncentiveService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Calls the Incentive API and retrieves incentive amount
    public double getIncentiveAmount(Transaction transaction) {
        try {
            String url = "http://localhost:8090/incentive";
            IncentiveResponse response = restTemplate.postForObject(url, transaction, IncentiveResponse.class);
            return response != null ? response.getAmount() : 0.0;
        } catch (Exception e) {
            System.err.println("⚠️ Error while calling Incentive API: " + e.getMessage());
            return 0.0;
        }
    }

    // Helper class for parsing JSON response
    private static class IncentiveResponse {
        private double amount;
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
    }
}
