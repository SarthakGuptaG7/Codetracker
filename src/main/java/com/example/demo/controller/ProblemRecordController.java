package com.example.demo.controller;

import com.example.demo.dto.ProblemRecordDto;
import com.example.demo.service.ProblemRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/problems")
public class ProblemRecordController {

    private final ProblemRecordService problemRecordService;

    public ProblemRecordController(ProblemRecordService problemRecordService) {
        this.problemRecordService = problemRecordService;
    }

    @PostMapping
    public ResponseEntity<ProblemRecordDto> addProblemRecord(
            Authentication authentication,
            @RequestBody ProblemRecordDto dto) {
        return ResponseEntity.ok(problemRecordService.addProblemRecord(authentication.getName(), dto));
    }

    @GetMapping
    public ResponseEntity<List<ProblemRecordDto>> getProblems(Authentication authentication) {
        return ResponseEntity.ok(problemRecordService.getUserProblemRecords(authentication.getName()));
    }
}
