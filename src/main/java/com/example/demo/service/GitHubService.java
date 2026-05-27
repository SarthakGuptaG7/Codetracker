package com.example.demo.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;
    private static final String GITHUB_API_URL = "https://api.github.com/users/";

    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchRecentEvents(String username) {
        String url = GITHUB_API_URL + username + "/events";
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            if (response.getBody() != null) {
                return (List<Map<String, Object>>) response.getBody();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch GitHub data for " + username + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
