package com.payitforward.platform.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;
    
    @Column(nullable = false)
    private Long posterId; // Foreign key to User
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private JobCategory category;
    
    @Column(length = 500)
    private String requiredSkills;
    
    @Column(precision = 4, scale = 2)
    private BigDecimal estimatedHours;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal creditCost;
    
    @Column(length = 200)
    private String location;
    
    @Column
    private Boolean isRemote = false;
    
    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgencyLevel = UrgencyLevel.MEDIUM;
    
    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.OPEN;
    
    private LocalDateTime deadline;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Long assignedToUserId; // Who is helping (once accepted)
    
    // Constructors
    public Job() {}
    
    public Job(String title, String description, BigDecimal creditCost, Long posterId) {
        this.title = title;
        this.description = description;
        this.creditCost = creditCost;
        this.posterId = posterId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public Long getPosterId() {
        return posterId;
    }
    
    public void setPosterId(Long posterId) {
        this.posterId = posterId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public JobCategory getCategory() {
        return category;
    }
    
    public void setCategory(JobCategory category) {
        this.category = category;
    }
    
    public String getRequiredSkills() {
        return requiredSkills;
    }
    
    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
    
    public BigDecimal getEstimatedHours() {
        return estimatedHours;
    }
    
    public void setEstimatedHours(BigDecimal estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
    
    public BigDecimal getCreditCost() {
        return creditCost;
    }
    
    public void setCreditCost(BigDecimal creditCost) {
        this.creditCost = creditCost;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Boolean getIsRemote() {
        return isRemote;
    }
    
    public void setIsRemote(Boolean isRemote) {
        this.isRemote = isRemote;
    }
    
    public UrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }
    
    public void setUrgencyLevel(UrgencyLevel urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }
    
    public JobStatus getStatus() {
        return status;
    }
    
    public void setStatus(JobStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getDeadline() {
        return deadline;
    }
    
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getAssignedToUserId() {
        return assignedToUserId;
    }
    
    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate  
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
