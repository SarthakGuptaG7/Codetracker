package com.example.demo.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeetCodeService {

    private final RestTemplate restTemplate;
    private static final String LEETCODE_API_URL = "https://leetcode.com/graphql";

    public LeetCodeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchRecentSubmissions(String username) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String query = "query recentAcSubmissionList($username: String!, $limit: Int!) { " +
                    "recentAcSubmissionList(username: $username, limit: $limit) { " +
                    "id title titleSlug timestamp } }";

            Map<String, Object> variables = new HashMap<>();
            variables.put("username", username);
            variables.put("limit", 100);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            requestBody.put("variables", variables);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(LEETCODE_API_URL, request, Map.class);

            if (response.getBody() != null && response.getBody().get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                if (data.get("recentAcSubmissionList") != null) {
                    return (List<Map<String, Object>>) data.get("recentAcSubmissionList");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch LeetCode data for " + username + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchStats(String username) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String query = "query getUserProfile($username: String!) { " +
                    "matchedUser(username: $username) { " +
                    "username " +
                    "submitStats { acSubmissionNum { difficulty count } } " +
                    "profile { ranking reputation } " +
                    "} }";

            Map<String, Object> variables = new HashMap<>();
            variables.put("username", username);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            requestBody.put("variables", variables);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(LEETCODE_API_URL, request, Map.class);

            if (response.getBody() != null && response.getBody().get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                if (data.get("matchedUser") != null) {
                    return (Map<String, Object>) data.get("matchedUser");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch LeetCode stats for " + username + ": " + e.getMessage());
        }
        return new HashMap<>();
    }
}
