package com.example.demo.service;

import com.example.demo.dto.StatsResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StatsService {

    private final UserRepository userRepository;
    private final LeetCodeService leetCodeService;
    private final GeeksForGeeksService geeksForGeeksService;
    private final HackerRankService hackerRankService;
    private final com.example.demo.repository.PlatformProfileRepository profileRepository;
    private final ObjectMapper objectMapper;

    public StatsService(
            UserRepository userRepository, 
            LeetCodeService leetCodeService, 
            GeeksForGeeksService geeksForGeeksService,
            HackerRankService hackerRankService,
            com.example.demo.repository.PlatformProfileRepository profileRepository, 
            ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.leetCodeService = leetCodeService;
        this.geeksForGeeksService = geeksForGeeksService;
        this.hackerRankService = hackerRankService;
        this.profileRepository = profileRepository;
        this.objectMapper = objectMapper;
    }

    private static final long CACHE_DURATION_MINUTES = 60;

    public StatsResponse getStatsForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return getStatsForUser(user);
    }

    public StatsResponse getStatsForUserId(Long userId) {
        return getStatsForUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
    }

    private StatsResponse getStatsForUser(User user) {
        List<StatsResponse.PlatformStat> platforms = new ArrayList<>();
        
        // LeetCode Stats
        platforms.add(getPlatformStats(user, "LeetCode", user.getLeetcodeUsername(), "#f59e0b"));

        // GeeksforGeeks Stats
        platforms.add(getPlatformStats(user, "GeeksforGeeks", user.getGeeksforgeeksUsername(), "#16a34a"));

        // HackerRank Stats
        platforms.add(getPlatformStats(user, "HackerRank", user.getHackerrankUsername(), "#10b981"));

        int totalSolved = platforms.stream().mapToInt(StatsResponse.PlatformStat::getSolved).sum();
        int totalEasy = platforms.stream().mapToInt(StatsResponse.PlatformStat::getEasy).sum();
        int totalMedium = platforms.stream().mapToInt(StatsResponse.PlatformStat::getMedium).sum();
        int totalHard = platforms.stream().mapToInt(StatsResponse.PlatformStat::getHard).sum();

        List<StatsResponse.DifficultyStat> difficulties = List.of(
            new StatsResponse.DifficultyStat("Easy", totalEasy),
            new StatsResponse.DifficultyStat("Medium", totalMedium),
            new StatsResponse.DifficultyStat("Hard", totalHard)
        );

        return StatsResponse.builder()
                .totalSolved(totalSolved)
                .codeScore(totalSolved * 5) // Simple formula for score
                .platforms(platforms)
                .difficulties(difficulties)
                .build();
    }

    private StatsResponse.PlatformStat getPlatformStats(User user, String platformName, String username, String color) {
        if (username == null || username.isEmpty()) return emptyPlatform(platformName, color);

        Optional<com.example.demo.model.PlatformProfile> profileOpt = profileRepository.findByUserAndPlatformName(user, platformName);
        com.example.demo.model.PlatformProfile profile;
        
        if (profileOpt.isPresent()) {
            profile = profileOpt.get();
            // Check if cache is fresh and username hasn't changed
            if (username.equals(profile.getPlatformUsername()) &&
                profile.getLastSyncedAt() != null && 
                profile.getLastSyncedAt().plusMinutes(CACHE_DURATION_MINUTES).isAfter(java.time.LocalDateTime.now()) &&
                profile.getCachedData() != null) {
                try {
                    return objectMapper.readValue(profile.getCachedData(), StatsResponse.PlatformStat.class);
                } catch (Exception e) {
                    System.err.println("Failed to read cache for " + platformName);
                }
            }
        } else {
            profile = new com.example.demo.model.PlatformProfile();
            profile.setUser(user);
            profile.setPlatformName(platformName);
            profile.setPlatformUsername(username);
        }

        // Fetch new stats
        StatsResponse.PlatformStat stats;
        if (platformName.equals("LeetCode")) stats = fetchLeetCodeStats(username);
        else if (platformName.equals("GeeksforGeeks")) stats = fetchGeeksForGeeksStats(username);
        else if (platformName.equals("HackerRank")) stats = fetchHackerRankStats(username);
        else stats = emptyPlatform(platformName, color);

        // Update cache
        try {
            profile.setCachedData(objectMapper.writeValueAsString(stats));
            profile.setLastSyncedAt(java.time.LocalDateTime.now());
            profile.setPlatformUsername(username);
            profileRepository.save(profile);
        } catch (Exception e) {
            System.err.println("Failed to update cache for " + platformName);
        }

        return stats;
    }

    private StatsResponse.PlatformStat fetchLeetCodeStats(String username) {
        try {
            Map<String, Object> stats = leetCodeService.fetchStats(username);
            if (stats == null || stats.isEmpty()) return emptyPlatform("LeetCode", "#f59e0b");

            Map<String, Object> submitStats = (Map<String, Object>) stats.get("submitStats");
            List<Map<String, Object>> acSubmissions = (List<Map<String, Object>>) submitStats.get("acSubmissionNum");
            
            int total = 0, easy = 0, medium = 0, hard = 0;
            for (Map<String, Object> sub : acSubmissions) {
                String difficulty = (String) sub.get("difficulty");
                int count = (int) sub.get("count");
                if (difficulty.equals("All")) total = count;
                else if (difficulty.equals("Easy")) easy = count;
                else if (difficulty.equals("Medium")) medium = count;
                else if (difficulty.equals("Hard")) hard = count;
            }

            Map<String, Object> profile = (Map<String, Object>) stats.get("profile");
            
            return StatsResponse.PlatformStat.builder()
                    .name("LeetCode")
                    .solved(total).easy(easy).medium(medium).hard(hard)
                    .color("#f59e0b")
                    .ranking(String.valueOf(profile.get("ranking")))
                    .contributionPoints(String.valueOf(profile.get("reputation")))
                    .recentSubmissions(leetCodeService.fetchRecentSubmissions(username))
                    .build();
        } catch (Exception e) {
            return emptyPlatform("LeetCode", "#f59e0b");
        }
    }

    private StatsResponse.PlatformStat fetchHackerRankStats(String username) {
        try {
            Map<String, Object> stats = hackerRankService.fetchStats(username);

            return StatsResponse.PlatformStat.builder()
                    .name("HackerRank")
                    .solved((int) stats.getOrDefault("solved", 0))
                    .color("#10b981")
                    .ranking(String.valueOf(stats.getOrDefault("rank", "")))
                    .badges((List<String>) stats.getOrDefault("badges", List.of()))
                    .build();
        } catch (Exception e) {
            return emptyPlatform("HackerRank", "#10b981");
        }
    }

    private StatsResponse.PlatformStat fetchGeeksForGeeksStats(String username) {
        try {
            Map<String, Object> stats = geeksForGeeksService.fetchStats(username);
            int solved = (int) stats.getOrDefault("solved", 0);

            return StatsResponse.PlatformStat.builder()
                    .name("GeeksforGeeks")
                    .solved(solved)
                    .easy(solved / 3).medium(solved / 3).hard(solved / 3)
                    .color("#16a34a")
                    .rating(String.valueOf(stats.getOrDefault("codingScore", "")))
                    .ranking(String.valueOf(stats.getOrDefault("instituteRank", "")))
                    .build();
        } catch (Exception e) {
            return emptyPlatform("GeeksforGeeks", "#16a34a");
        }
    }

    private StatsResponse.PlatformStat emptyPlatform(String name, String color) {
        return StatsResponse.PlatformStat.builder()
                .name(name).solved(0).easy(0).medium(0).hard(0).color(color)
                .build();
    }

}
