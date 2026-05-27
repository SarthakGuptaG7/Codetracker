package com.example.demo.controller;

import com.example.demo.service.GeeksForGeeksService;
import com.example.demo.service.GitHubService;
import com.example.demo.service.HackerRankService;
import com.example.demo.service.LeetCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationController {

    private final GitHubService gitHubService;
    private final GeeksForGeeksService geeksForGeeksService;
    private final HackerRankService hackerRankService;
    private final LeetCodeService leetCodeService;

    public IntegrationController(GitHubService gitHubService, GeeksForGeeksService geeksForGeeksService, HackerRankService hackerRankService, LeetCodeService leetCodeService) {
        this.gitHubService = gitHubService;
        this.geeksForGeeksService = geeksForGeeksService;
        this.hackerRankService = hackerRankService;
        this.leetCodeService = leetCodeService;
    }

    @GetMapping("/github/{username}")
    public ResponseEntity<List<Map<String, Object>>> getGitHubEvents(@PathVariable String username) {
        return ResponseEntity.ok(gitHubService.fetchRecentEvents(username));
    }

    @GetMapping("/geeksforgeeks/{username}/stats")
    public ResponseEntity<Map<String, Object>> getGeeksForGeeksStats(@PathVariable String username) {
        return ResponseEntity.ok(geeksForGeeksService.fetchStats(username));
    }

    @GetMapping("/hackerrank/{username}/stats")
    public ResponseEntity<Map<String, Object>> getHackerRankStats(@PathVariable String username) {
        return ResponseEntity.ok(hackerRankService.fetchStats(username));
    }

    @GetMapping("/leetcode/{username}")
    public ResponseEntity<List<Map<String, Object>>> getLeetCodeData(@PathVariable String username) {
        return ResponseEntity.ok(leetCodeService.fetchRecentSubmissions(username));
    }

    @GetMapping("/leetcode/{username}/stats")
    public ResponseEntity<Map<String, Object>> getLeetCodeStats(@PathVariable String username) {
        return ResponseEntity.ok(leetCodeService.fetchStats(username));
    }
}
