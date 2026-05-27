package com.example.demo.controller;

import com.example.demo.dto.StatsResponse;
import com.example.demo.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/me")
    public ResponseEntity<StatsResponse> getMyStats() {
        return ResponseEntity.ok(statsService.getStatsForCurrentUser());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<StatsResponse> getUserStats(@PathVariable Long userId) {
        return ResponseEntity.ok(statsService.getStatsForUserId(userId));
    }
}
