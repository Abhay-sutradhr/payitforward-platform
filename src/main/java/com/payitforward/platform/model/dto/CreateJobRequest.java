package com.payitforward.platform.model.dto;

import com.payitforward.platform.model.entity.JobCategory;
import com.payitforward.platform.model.entity.UrgencyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateJobRequest {
    @NotBlank(message = "Job title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Job description is required")
    private String description;
    
    @NotNull(message = "Credit cost is required")
    @DecimalMin(value = "0.5", message = "Credit cost must be at least 0.5")
    private BigDecimal creditCost;
    
    private JobCategory category;
    private String requiredSkills;
    private BigDecimal estimatedHours;
    private String location;
    private Boolean isRemote = false;
    private UrgencyLevel urgencyLevel = UrgencyLevel.MEDIUM;
    private LocalDateTime deadline;
    
    // Constructors
    public CreateJobRequest() {}
    
    // Getters and Setters
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
    
    public BigDecimal getCreditCost() {
        return creditCost;
    }
    
    public void setCreditCost(BigDecimal creditCost) {
        this.creditCost = creditCost;
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
    
    public LocalDateTime getDeadline() {
        return deadline;
    }
    
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
