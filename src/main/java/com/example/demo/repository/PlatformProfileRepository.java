package com.example.demo.repository;

import com.example.demo.model.PlatformProfile;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformProfileRepository extends JpaRepository<PlatformProfile, Long> {
    List<PlatformProfile> findByUser(User user);
    Optional<PlatformProfile> findByUserAndPlatformName(User user, String platformName);
}
