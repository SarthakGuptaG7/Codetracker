package com.example.demo.dto;

import java.time.LocalDateTime;

public class ProblemRecordDto {
    private Long id;
    private String platform;
    private String problemId;
    private String problemTitle;
    private String difficultyLevel;
    private LocalDateTime solvedAt;
    private String tags;
    private String notes;
    private Boolean isManualEntry;

    public ProblemRecordDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
