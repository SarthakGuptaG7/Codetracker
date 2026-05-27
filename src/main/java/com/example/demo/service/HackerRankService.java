package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HackerRankService {

    private final RestTemplate restTemplate;

    public HackerRankService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchStats(String username) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("solved", 0);
        stats.put("rank", "");
        stats.put("badges", List.of());

        if (username == null || username.isBlank()) {
            return stats;
        }

        try {
            String url = "https://www.hackerrank.com/rest/hackers/" + username.trim() + "/profile";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.get("model") == null) {
                return stats;
            }

            Map<String, Object> model = (Map<String, Object>) response.get("model");
            stats.put("solved", model.getOrDefault("solved_challenges", 0));
            stats.put("rank", model.getOrDefault("rank", ""));
            stats.put("badges", model.getOrDefault("badges", List.of()));
        } catch (Exception e) {
            System.err.println("Failed to fetch HackerRank data for " + username + ": " + e.getMessage());
        }

        return stats;
    }
}
