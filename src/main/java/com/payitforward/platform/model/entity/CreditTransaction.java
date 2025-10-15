package com.payitforward.platform.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_transactions")
public class CreditTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    
    @Column(nullable = false)
    private Long fromUserId; // Who is paying (job poster)
    
    @Column(nullable = false)
    private Long toUserId; // Who is receiving (helper)
    
    @Column(nullable = false)
    private Long jobId; // Related job
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // Amount transferred
    
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String description; // Reason for transaction
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    // Constructors
    public CreditTransaction() {}
    
    public CreditTransaction(Long fromUserId, Long toUserId, Long jobId, 
                           BigDecimal amount, TransactionType type, String description) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.jobId = jobId;
        this.amount = amount;
        this.transactionType = type;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
    
    public Long getFromUserId() {
        return fromUserId;
    }
    
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
    
    public Long getToUserId() {
        return toUserId;
    }
    
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
    
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
