package com.payitforward.platform.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserProfileResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private BigDecimal totalCredits;
    private Integer totalJobsCompleted;
    private Integer totalJobsPosted;
    private BigDecimal averageRating;
    private LocalDateTime createdAt;
    
    // Default constructor
    public UserProfileResponse() {}
    
    // Constructor with all fields
    public UserProfileResponse(Long userId, String username, String email, String fullName, 
                              String phone, String address, BigDecimal totalCredits, 
                              Integer totalJobsCompleted, Integer totalJobsPosted, 
                              BigDecimal averageRating, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.totalCredits = totalCredits;
        this.totalJobsCompleted = totalJobsCompleted;
        this.totalJobsPosted = totalJobsPosted;
        this.averageRating = averageRating;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public BigDecimal getTotalCredits() {
        return totalCredits;
    }
    
    public void setTotalCredits(BigDecimal totalCredits) {
        this.totalCredits = totalCredits;
    }
    
    public Integer getTotalJobsCompleted() {
        return totalJobsCompleted;
    }
    
    public void setTotalJobsCompleted(Integer totalJobsCompleted) {
        this.totalJobsCompleted = totalJobsCompleted;
    }
    
    public Integer getTotalJobsPosted() {
        return totalJobsPosted;
    }
    
    public void setTotalJobsPosted(Integer totalJobsPosted) {
        this.totalJobsPosted = totalJobsPosted;
    }
    
    public BigDecimal getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
