package com.example.demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_submissions")
public class TestSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer marks;

    @Column(columnDefinition = "TEXT")
    private String codeSubmitted;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"room", "password", "authorities"})
    private User student;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    @JsonIgnoreProperties({"room"})
    private Test test;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    public TestSubmission() {}

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }

    public String getCodeSubmitted() { return codeSubmitted; }
    public void setCodeSubmitted(String codeSubmitted) { this.codeSubmitted = codeSubmitted; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Test getTest() { return test; }
    public void setTest(Test test) { this.test = test; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
