package com.example.demo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeeksForGeeksService {

    public Map<String, Object> fetchStats(String username) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("solved", 0);
        stats.put("codingScore", "");
        stats.put("instituteRank", "");

        if (username == null || username.isBlank()) {
            return stats;
        }

        try {
            Document doc = Jsoup.connect("https://www.geeksforgeeks.org/user/" + username.trim() + "/")
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            String text = doc.text();
            stats.put("solved", extractFirstNumber(text, "Problem Solved", "Problems Solved", "Solved Problems"));
            stats.put("codingScore", String.valueOf(extractFirstNumber(text, "Coding Score")));
            stats.put("instituteRank", String.valueOf(extractFirstNumber(text, "Institute Rank")));
        } catch (Exception e) {
            System.err.println("Failed to fetch GeeksforGeeks data for " + username + ": " + e.getMessage());
        }

        return stats;
    }

    private int extractFirstNumber(String text, String... labels) {
        for (String label : labels) {
            Pattern pattern = Pattern.compile(label + "\\D{0,40}(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return 0;
    }
}
