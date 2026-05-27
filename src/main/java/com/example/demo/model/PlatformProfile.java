package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "platform_profiles")
public class PlatformProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "platform_name", nullable = false)
    private String platformName;

    @Column(name = "platform_username", nullable = false)
    private String platformUsername;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
    
    @Column(columnDefinition = "TEXT")
    private String syncToken;

    @Column(name = "cached_data", columnDefinition = "TEXT")
    private String cachedData;

    public PlatformProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }
    
    public String getPlatformUsername() { return platformUsername; }
    public void setPlatformUsername(String platformUsername) { this.platformUsername = platformUsername; }
    
    public LocalDateTime getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(LocalDateTime lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }
    
    public String getSyncToken() { return syncToken; }
    public void setSyncToken(String syncToken) { this.syncToken = syncToken; }

    public String getCachedData() { return cachedData; }
    public void setCachedData(String cachedData) { this.cachedData = cachedData; }
}
