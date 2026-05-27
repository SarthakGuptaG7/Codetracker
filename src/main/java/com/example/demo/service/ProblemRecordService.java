package com.example.demo.service;

import com.example.demo.dto.ProblemRecordDto;
import com.example.demo.model.ProblemRecord;
import com.example.demo.model.User;
import com.example.demo.repository.ProblemRecordRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProblemRecordService {

    private final ProblemRecordRepository repository;
    private final UserRepository userRepository;

    public ProblemRecordService(ProblemRecordRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public ProblemRecordDto addProblemRecord(String username, ProblemRecordDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProblemRecord record = new ProblemRecord();
        record.setUser(user);
        record.setPlatform(dto.getPlatform());
        record.setProblemId(dto.getProblemId());
        record.setProblemTitle(dto.getProblemTitle());
        record.setDifficultyLevel(dto.getDifficultyLevel());
        record.setSolvedAt(dto.getSolvedAt() != null ? dto.getSolvedAt() : LocalDateTime.now());
        record.setTags(dto.getTags());
        record.setNotes(dto.getNotes());
        record.setIsManualEntry(dto.getIsManualEntry() != null ? dto.getIsManualEntry() : true);

        ProblemRecord saved = repository.save(record);
        dto.setId(saved.getId());
        return dto;
    }

    public List<ProblemRecordDto> getUserProblemRecords(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return repository.findByUserId(user.getId()).stream()
                .map(record -> {
                    ProblemRecordDto dto = new ProblemRecordDto();
                    dto.setId(record.getId());
                    dto.setPlatform(record.getPlatform());
                    dto.setProblemId(record.getProblemId());
                    dto.setProblemTitle(record.getProblemTitle());
                    dto.setDifficultyLevel(record.getDifficultyLevel());
                    dto.setSolvedAt(record.getSolvedAt());
                    dto.setTags(record.getTags());
                    dto.setNotes(record.getNotes());
                    dto.setIsManualEntry(record.getIsManualEntry());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
