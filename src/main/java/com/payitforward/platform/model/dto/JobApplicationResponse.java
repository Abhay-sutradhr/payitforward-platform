package com.payitforward.platform.model.dto;

import com.payitforward.platform.model.entity.ApplicationStatus;
import java.time.LocalDateTime;

public class JobApplicationResponse {
    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private Long applicantId;
    private String applicantUsername;
    private String applicantFullName;
    private String message;
    private String qualifications;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public JobApplicationResponse() {}
    
    // Getters and Setters
    public Long getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
    
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public Long getApplicantId() {
        return applicantId;
    }
    
    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }
    
    public String getApplicantUsername() {
        return applicantUsername;
    }
    
    public void setApplicantUsername(String applicantUsername) {
        this.applicantUsername = applicantUsername;
    }
    
    public String getApplicantFullName() {
        return applicantFullName;
    }
    
    public void setApplicantFullName(String applicantFullName) {
        this.applicantFullName = applicantFullName;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getQualifications() {
        return qualifications;
    }
    
    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }
    
    public ApplicationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
    
    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
