package com.payitforward.platform.model.dto;

import com.payitforward.platform.model.entity.JobCategory;
import com.payitforward.platform.model.entity.JobStatus;
import com.payitforward.platform.model.entity.UrgencyLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JobResponse {
    private Long jobId;
    private Long posterId;
    private String posterUsername;
    private String title;
    private String description;
    private JobCategory category;
    private String requiredSkills;
    private BigDecimal estimatedHours;
    private BigDecimal creditCost;
    private String location;
    private Boolean isRemote;
    private UrgencyLevel urgencyLevel;
    private JobStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long assignedToUserId;
    private String assignedToUsername;
    
    // Constructors
    public JobResponse() {}
    
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
    
    public String getPosterUsername() {
        return posterUsername;
    }
    
    public void setPosterUsername(String posterUsername) {
        this.posterUsername = posterUsername;
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
    
    public String getAssignedToUsername() {
        return assignedToUsername;
    }
    
    public void setAssignedToUsername(String assignedToUsername) {
        this.assignedToUsername = assignedToUsername;
    }
}
