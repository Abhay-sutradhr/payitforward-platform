package com.payitforward.platform.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ApplyToJobRequest {
    @NotBlank(message = "Application message is required")
    @Size(min = 10, max = 1000, message = "Message must be between 10 and 1000 characters")
    private String message;
    
    @Size(max = 500, message = "Qualifications cannot exceed 500 characters")
    private String qualifications;
    
    // Constructors
    public ApplyToJobRequest() {}
    
    // Getters and Setters
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
}
