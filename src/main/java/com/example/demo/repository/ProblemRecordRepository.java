package com.example.demo.repository;

import com.example.demo.model.ProblemRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRecordRepository extends JpaRepository<ProblemRecord, Long> {
    List<ProblemRecord> findByUserId(Long userId);
    List<ProblemRecord> findByUserIdAndPlatform(Long userId, String platform);
}
