package com.payitforward.platform.service;

import com.payitforward.platform.model.dto.ApplyToJobRequest;
import com.payitforward.platform.model.dto.JobApplicationResponse;
import com.payitforward.platform.model.entity.*;
import com.payitforward.platform.repository.JobApplicationRepository;
import com.payitforward.platform.repository.JobRepository;
import com.payitforward.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {
    
    @Autowired
    private JobApplicationRepository applicationRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public JobApplication applyToJob(Long jobId, Long applicantId, ApplyToJobRequest request) {
        // Check if job exists and is available
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (job.getStatus() != JobStatus.OPEN) {
            throw new RuntimeException("Job is not available for applications");
        }
        
        // Check if user is trying to apply to their own job
        if (job.getPosterId().equals(applicantId)) {
            throw new RuntimeException("You cannot apply to your own job");
        }
        
        // Check if user already applied
        if (applicationRepository.existsByJobIdAndApplicantId(jobId, applicantId)) {
            throw new RuntimeException("You have already applied to this job");
        }
        
        // Check if applicant exists
        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create application
        JobApplication application = new JobApplication();
        application.setJobId(jobId);
        application.setApplicantId(applicantId);
        application.setMessage(request.getMessage());
        application.setQualifications(request.getQualifications());
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        
        return applicationRepository.save(application);
    }
    
    @Transactional
    public JobApplication acceptApplication(Long applicationId, Long jobPosterId) {
        // Find the application
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Find the job
        Job job = jobRepository.findById(application.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Check if the user is the job poster
        if (!job.getPosterId().equals(jobPosterId)) {
            throw new RuntimeException("You can only accept applications for your own jobs");
        }
        
        // Check if job is still open
        if (job.getStatus() != JobStatus.OPEN) {
            throw new RuntimeException("Job is no longer available");
        }
        
        // Accept this application
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setReviewedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        
        // Update job status and assign user
        job.setStatus(JobStatus.ASSIGNED);
        job.setAssignedToUserId(application.getApplicantId());
        job.setUpdatedAt(LocalDateTime.now());
        
        // Reject all other pending applications for this job
        List<JobApplication> otherApplications = applicationRepository
                .findByJobIdAndStatus(job.getJobId(), ApplicationStatus.PENDING);
        
        for (JobApplication otherApp : otherApplications) {
            if (!otherApp.getApplicationId().equals(applicationId)) {
                otherApp.setStatus(ApplicationStatus.REJECTED);
                otherApp.setReviewedAt(LocalDateTime.now());
                otherApp.setUpdatedAt(LocalDateTime.now());
                applicationRepository.save(otherApp);
            }
        }
        
        // Save changes
        jobRepository.save(job);
        return applicationRepository.save(application);
    }
    
    public List<JobApplicationResponse> getApplicationsForJob(Long jobId, Long jobPosterId) {
        // Verify the user owns this job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getPosterId().equals(jobPosterId)) {
            throw new RuntimeException("You can only view applications for your own jobs");
        }
        
        List<JobApplication> applications = applicationRepository
                .findByJobIdOrderByAppliedAtDesc(jobId);
        
        return applications.stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());
    }
    
    public List<JobApplicationResponse> getMyApplications(Long applicantId) {
        List<JobApplication> applications = applicationRepository
                .findByApplicantIdOrderByAppliedAtDesc(applicantId);
        
        return applications.stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());
    }
    
    public JobApplicationResponse convertToApplicationResponse(JobApplication application) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.setApplicationId(application.getApplicationId());
        response.setJobId(application.getJobId());
        response.setApplicantId(application.getApplicantId());
        response.setMessage(application.getMessage());
        response.setQualifications(application.getQualifications());
        response.setStatus(application.getStatus());
        response.setAppliedAt(application.getAppliedAt());
        response.setReviewedAt(application.getReviewedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        
        // Get job title
        Optional<Job> job = jobRepository.findById(application.getJobId());
        job.ifPresent(j -> response.setJobTitle(j.getTitle()));
        
        // Get applicant details
        Optional<User> applicant = userRepository.findById(application.getApplicantId());
        applicant.ifPresent(user -> {
            response.setApplicantUsername(user.getUsername());
            response.setApplicantFullName(user.getFullName());
        });
        
        return response;
    }
    
 // Add this method for rejecting applications
    @Transactional
    public JobApplication rejectApplication(Long applicationId, Long jobPosterId) {
        // Find the application
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Find the job
        Job job = jobRepository.findById(application.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Check if the user is the job poster
        if (!job.getPosterId().equals(jobPosterId)) {
            throw new RuntimeException("You can only reject applications for your own jobs");
        }
        
        // Check if application can be rejected
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("Only pending applications can be rejected");
        }
        
        // Reject this application
        application.setStatus(ApplicationStatus.REJECTED);
        application.setReviewedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        
        return applicationRepository.save(application);
    }

    // Add this method for withdrawing applications
    @Transactional
    public JobApplication withdrawApplication(Long applicationId, Long applicantUserId) {
        // Find the application
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Check if the user owns this application
        if (!application.getApplicantId().equals(applicantUserId)) {
            throw new RuntimeException("You can only withdraw your own applications");
        }
        
        // Check if application can be withdrawn
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("Only pending applications can be withdrawn");
        }
        
        // Withdraw this application
        application.setStatus(ApplicationStatus.WITHDRAWN);
        application.setUpdatedAt(LocalDateTime.now());
        
        return applicationRepository.save(application);
    }

    // Add this method for getting single application by ID
    public JobApplicationResponse getApplicationById(Long applicationId, Long currentUserId) {
        // Find the application
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Find the job to check authorization
        Job job = jobRepository.findById(application.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Check if user is authorized (either applicant or job poster)
        if (!application.getApplicantId().equals(currentUserId) && 
            !job.getPosterId().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to view this application");
        }
        
        return convertToApplicationResponse(application);
    }

}
