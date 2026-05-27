package com.example.demo.controller;

import com.example.demo.model.Test;
import com.example.demo.model.TestSubmission;
import com.example.demo.model.User;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.TestRepository;
import com.example.demo.repository.TestSubmissionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tests")
@CrossOrigin(origins = "*")
public class TestController {

    private final TestRepository testRepository;
    private final TestSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public TestController(TestRepository testRepository, TestSubmissionRepository submissionRepository, UserRepository userRepository, RoomRepository roomRepository) {
        this.testRepository = testRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    @PostMapping
    public ResponseEntity<Test> createTest(@RequestBody Test testRequest) {
        if (testRequest.getRoom() == null || testRequest.getRoom().getId() == null) {
            throw new RuntimeException("Room is required");
        }
        testRequest.setRoom(roomRepository.findById(testRequest.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("Room not found")));
        if (testRequest.getStatus() == null || testRequest.getStatus().isBlank()) {
            testRequest.setStatus("available");
        }
        if (testRequest.getQuestions() != null) {
            testRequest.getQuestions().forEach(question -> question.setTest(testRequest));
        }
        return ResponseEntity.ok(testRepository.save(testRequest));
    }

    @GetMapping("/{testId}")
    public ResponseEntity<Test> getTest(@PathVariable Long testId) {
        return ResponseEntity.ok(testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found")));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Test>> getTestsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(testRepository.findByRoomId(roomId));
    }

    @PostMapping("/{testId}/submit")
    public ResponseEntity<TestSubmission> submitTest(@PathVariable Long testId, @RequestBody TestSubmission submissionRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username).orElseThrow();
        Test test = testRepository.findById(testId).orElseThrow();

        submissionRequest.setStudent(student);
        submissionRequest.setTest(test);

        return ResponseEntity.ok(submissionRepository.save(submissionRequest));
    }

    @GetMapping("/{testId}/submissions")
    public ResponseEntity<List<TestSubmission>> getSubmissions(@PathVariable Long testId) {
        return ResponseEntity.ok(submissionRepository.findByTestId(testId));
    }

    @GetMapping("/submissions/me")
    public ResponseEntity<List<TestSubmission>> getMySubmissions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(submissionRepository.findByStudentId(student.getId()));
    }
}
