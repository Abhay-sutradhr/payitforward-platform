package com.payitforward.platform.service;

import com.payitforward.platform.model.dto.CreateJobRequest;
import com.payitforward.platform.model.dto.JobResponse;
import com.payitforward.platform.model.entity.Job;
import com.payitforward.platform.model.entity.JobStatus;
import com.payitforward.platform.model.entity.User;
import com.payitforward.platform.repository.JobRepository;
import com.payitforward.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Job createJob(CreateJobRequest request, Long posterId) {
        // Check if user has enough credits
        User poster = userRepository.findById(posterId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (poster.getTotalCredits().compareTo(request.getCreditCost()) < 0) {
            throw new RuntimeException("Insufficient credits to post this job");
        }
        
        // Create new job
        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCreditCost(request.getCreditCost());
        job.setPosterId(posterId);
        job.setCategory(request.getCategory());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setEstimatedHours(request.getEstimatedHours());
        job.setLocation(request.getLocation());
        job.setIsRemote(request.getIsRemote());
        job.setUrgencyLevel(request.getUrgencyLevel());
        job.setDeadline(request.getDeadline());
        job.setStatus(JobStatus.OPEN);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        
        // Deduct credits from poster (they pay upfront)
        poster.setTotalCredits(poster.getTotalCredits().subtract(request.getCreditCost()));
        poster.setTotalJobsPosted(poster.getTotalJobsPosted() + 1);
        userRepository.save(poster);
        
        return jobRepository.save(job);
    }
    
    public List<JobResponse> getAllAvailableJobs() {
        List<Job> jobs = jobRepository.findAvailableJobs();
        return jobs.stream().map(this::convertToJobResponse).collect(Collectors.toList());
    }
    
    public List<JobResponse> getJobsByPoster(Long posterId) {
        List<Job> jobs = jobRepository.findByPosterIdOrderByCreatedAtDesc(posterId);
        return jobs.stream().map(this::convertToJobResponse).collect(Collectors.toList());
    }
    
    public List<JobResponse> getAssignedJobs(Long userId) {
        List<Job> jobs = jobRepository.findByAssignedToUserId(userId);
        return jobs.stream().map(this::convertToJobResponse).collect(Collectors.toList());
    }
    
    public Optional<Job> getJobById(Long jobId) {
        return jobRepository.findById(jobId);
    }
    
    public JobResponse convertToJobResponse(Job job) {
        JobResponse response = new JobResponse();
        response.setJobId(job.getJobId());
        response.setPosterId(job.getPosterId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setCategory(job.getCategory());
        response.setRequiredSkills(job.getRequiredSkills());
        response.setEstimatedHours(job.getEstimatedHours());
        response.setCreditCost(job.getCreditCost());
        response.setLocation(job.getLocation());
        response.setIsRemote(job.getIsRemote());
        response.setUrgencyLevel(job.getUrgencyLevel());
        response.setStatus(job.getStatus());
        response.setDeadline(job.getDeadline());
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());
        response.setAssignedToUserId(job.getAssignedToUserId());
        
        // Get poster username
        Optional<User> poster = userRepository.findById(job.getPosterId());
        poster.ifPresent(user -> response.setPosterUsername(user.getUsername()));
        
        // Get assigned user username
        if (job.getAssignedToUserId() != null) {
            Optional<User> assignedUser = userRepository.findById(job.getAssignedToUserId());
            assignedUser.ifPresent(user -> response.setAssignedToUsername(user.getUsername()));
        }
        
        return response;
    }
}
