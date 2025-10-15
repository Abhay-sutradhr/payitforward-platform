package com.payitforward.platform.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class CompleteJobRequest {
    @Size(max = 500, message = "Completion notes cannot exceed 500 characters")
    private String completionNotes;
    
    @DecimalMin(value = "0.0", message = "Bonus amount cannot be negative")
    private BigDecimal bonusAmount = BigDecimal.ZERO;
    
    private Integer rating; // 1-5 star rating for helper
    
    @Size(max = 200, message = "Review cannot exceed 200 characters")
    private String review;
    
    // Constructors
    public CompleteJobRequest() {}
    
    // Getters and Setters
    public String getCompletionNotes() {
        return completionNotes;
    }
    
    public void setCompletionNotes(String completionNotes) {
        this.completionNotes = completionNotes;
    }
    
    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }
    
    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getReview() {
        return review;
    }
    
    public void setReview(String review) {
        this.review = review;
    }
}
