package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsResponse {
    private int totalSolved;
    private int codeScore;
    private List<PlatformStat> platforms;
    private List<DifficultyStat> difficulties;
    private List<ActivityPoint> recentActivity;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlatformStat {
        private String name;
        private int solved;
        private String color;
        private int easy;
        private int medium;
        private int hard;
        
        // Extended stats
        private String rating;
        private String ranking;
        private String globalRank;
        private String stars;
        private List<String> badges;
        private List<String> skills;
        private String contributionPoints;
        private List<Map<String, Object>> recentSubmissions;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DifficultyStat {
        private String name;
        private int value;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActivityPoint {
        private String date;
        private int count;
    }
}
