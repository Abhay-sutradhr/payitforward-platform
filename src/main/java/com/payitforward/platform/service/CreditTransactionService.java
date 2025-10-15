package com.payitforward.platform.service;

import com.payitforward.platform.model.dto.CompleteJobRequest;
import com.payitforward.platform.model.dto.CreditTransactionResponse;
import com.payitforward.platform.model.entity.*;
import com.payitforward.platform.repository.CreditTransactionRepository;
import com.payitforward.platform.repository.JobRepository;
import com.payitforward.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CreditTransactionService {
    
    @Autowired
    private CreditTransactionRepository transactionRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public CreditTransaction completeJobAndTransferCredits(Long jobId, Long jobPosterId, CompleteJobRequest request) {
        // Find the job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Verify the user is the job poster
        if (!job.getPosterId().equals(jobPosterId)) {
            throw new RuntimeException("You can only complete your own jobs");
        }
        
        // Check if job is assigned
        if (job.getStatus() != JobStatus.ASSIGNED) {
            throw new RuntimeException("Job must be assigned to be completed");
        }
        
        if (job.getAssignedToUserId() == null) {
            throw new RuntimeException("No helper assigned to this job");
        }
        
        // Check if already completed
        Optional<CreditTransaction> existingTransaction = transactionRepository
                .findByJobIdAndTransactionType(jobId, TransactionType.JOB_PAYMENT);
        if (existingTransaction.isPresent()) {
            throw new RuntimeException("Job has already been completed");
        }
        
        // Get job poster and helper
        User jobPoster = userRepository.findById(jobPosterId)
                .orElseThrow(() -> new RuntimeException("Job poster not found"));
        
        User helper = userRepository.findById(job.getAssignedToUserId())
                .orElseThrow(() -> new RuntimeException("Helper not found"));
        
        // Calculate total payment (job cost + bonus)
        BigDecimal totalPayment = job.getCreditCost().add(request.getBonusAmount());
        
        // Create transaction record
        CreditTransaction transaction = new CreditTransaction(
                jobPosterId,
                job.getAssignedToUserId(),
                jobId,
                totalPayment,
                TransactionType.JOB_PAYMENT,
                "Payment for completed job: " + job.getTitle()
        );
        
        // Transfer credits (credits were already deducted from poster when job was created)
        helper.setTotalCredits(helper.getTotalCredits().add(totalPayment));
        helper.setTotalJobsCompleted(helper.getTotalJobsCompleted() + 1);
        
        // Update job status
        job.setStatus(JobStatus.COMPLETED);
        job.setUpdatedAt(LocalDateTime.now());
        
        // Update job poster stats
        jobPoster.setUpdatedAt(LocalDateTime.now());
        
        // Mark transaction as completed
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        
        // Save all changes
        userRepository.save(helper);
        userRepository.save(jobPoster);
        jobRepository.save(job);
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public CreditTransaction cancelJobAndRefund(Long jobId, Long jobPosterId, String reason) {
        // Find the job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Verify the user is the job poster
        if (!job.getPosterId().equals(jobPosterId)) {
            throw new RuntimeException("You can only cancel your own jobs");
        }
        
        // Check if job can be cancelled
        if (job.getStatus() == JobStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed job");
        }
        
        if (job.getStatus() == JobStatus.CANCELLED) {
            throw new RuntimeException("Job is already cancelled");
        }
        
        // Get job poster
        User jobPoster = userRepository.findById(jobPosterId)
                .orElseThrow(() -> new RuntimeException("Job poster not found"));
        
        // Refund credits to job poster
        jobPoster.setTotalCredits(jobPoster.getTotalCredits().add(job.getCreditCost()));
        
        // Create refund transaction
        CreditTransaction refundTransaction = new CreditTransaction(
                null, // No sender for refund
                jobPosterId,
                jobId,
                job.getCreditCost(),
                TransactionType.JOB_REFUND,
                "Refund for cancelled job: " + job.getTitle() + ". Reason: " + reason
        );
        
        // Update job status
        job.setStatus(JobStatus.CANCELLED);
        job.setUpdatedAt(LocalDateTime.now());
        
        // Mark transaction as completed
        refundTransaction.setStatus(TransactionStatus.COMPLETED);
        refundTransaction.setCompletedAt(LocalDateTime.now());
        
        // Save changes
        userRepository.save(jobPoster);
        jobRepository.save(job);
        
        return transactionRepository.save(refundTransaction);
    }
    
    public List<CreditTransactionResponse> getUserTransactionHistory(Long userId) {
        List<CreditTransaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
    }
    
    public BigDecimal getUserTotalEarnings(Long userId) {
        return transactionRepository.getTotalReceivedByUser(userId);
    }
    
    public BigDecimal getUserTotalSpent(Long userId) {
        return transactionRepository.getTotalSentByUser(userId);
    }
    
    public CreditTransactionResponse convertToTransactionResponse(CreditTransaction transaction) {
        CreditTransactionResponse response = new CreditTransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setFromUserId(transaction.getFromUserId());
        response.setToUserId(transaction.getToUserId());
        response.setJobId(transaction.getJobId());
        response.setAmount(transaction.getAmount());
        response.setTransactionType(transaction.getTransactionType());
        response.setStatus(transaction.getStatus());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setCompletedAt(transaction.getCompletedAt());
        
        // Get usernames
        if (transaction.getFromUserId() != null) {
            Optional<User> fromUser = userRepository.findById(transaction.getFromUserId());
            fromUser.ifPresent(user -> response.setFromUsername(user.getUsername()));
        }
        
        if (transaction.getToUserId() != null) {
            Optional<User> toUser = userRepository.findById(transaction.getToUserId());
            toUser.ifPresent(user -> response.setToUsername(user.getUsername()));
        }
        
        // Get job title
        if (transaction.getJobId() != null) {
            Optional<Job> job = jobRepository.findById(transaction.getJobId());
            job.ifPresent(j -> response.setJobTitle(j.getTitle()));
        }
        
        return response;
    }
}
