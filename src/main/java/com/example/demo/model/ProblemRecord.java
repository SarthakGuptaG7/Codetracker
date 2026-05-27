package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_records")
public class ProblemRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String platform;

    @Column(name = "problem_id", nullable = false)
    private String problemId;

    @Column(name = "problem_title", nullable = false)
    private String problemTitle;

    @Column(name = "difficulty_level")
    private String difficultyLevel;

    @Column(name = "solved_at", nullable = false)
    private LocalDateTime solvedAt;

    private String tags;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_manual_entry", nullable = false)
    private Boolean isManualEntry = false;

    public ProblemRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getProblemId() { return problemId; }
    public void setProblemId(String problemId) { this.problemId = problemId; }

    public String getProblemTitle() { return problemTitle; }
    public void setProblemTitle(String problemTitle) { this.problemTitle = problemTitle; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public LocalDateTime getSolvedAt() { return solvedAt; }
    public void setSolvedAt(LocalDateTime solvedAt) { this.solvedAt = solvedAt; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getIsManualEntry() { return isManualEntry; }
    public void setIsManualEntry(Boolean isManualEntry) { this.isManualEntry = isManualEntry; }
}
