package com.payitforward.platform.controller;

import com.payitforward.platform.model.dto.CreateJobRequest;
import com.payitforward.platform.model.dto.JobResponse;
import com.payitforward.platform.model.entity.Job;
import com.payitforward.platform.model.entity.JobCategory;
import com.payitforward.platform.model.entity.JobStatus;
import com.payitforward.platform.service.JobService;
import com.payitforward.platform.service.UserService;
import com.payitforward.platform.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // Helper method to get user ID from JWT token
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null) {
                return userService.findByUsernameOrEmail(username)
                        .map(user -> user.getUserId())
                        .orElse(null);
            }
        }
        return null;
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createJob(@Valid @RequestBody CreateJobRequest request, 
                                      HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body("Authentication required");
            }
            
            Job job = jobService.createJob(request, userId);
            JobResponse response = jobService.convertToJobResponse(job);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Job creation failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<JobResponse>> getAvailableJobs() {
        try {
            List<JobResponse> jobs = jobService.getAllAvailableJobs();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/my-jobs")
    public ResponseEntity<?> getMyJobs(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("Authentication required");
            }
            
            List<JobResponse> jobs = jobService.getJobsByPoster(userId);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch jobs: " + e.getMessage());
        }
    }
    
    @GetMapping("/assigned")
    public ResponseEntity<?> getAssignedJobs(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("Authentication required");
            }
            
            List<JobResponse> jobs = jobService.getAssignedJobs(userId);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch assigned jobs: " + e.getMessage());
        }
    }
    
    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {
        try {
            Optional<Job> job = jobService.getJobById(jobId);
            if (job.isPresent()) {
                JobResponse response = jobService.convertToJobResponse(job.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch job: " + e.getMessage());
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<JobResponse>> getJobsByCategory(@PathVariable String category) {
        try {
            // Convert string to enum (this is a simplified version)
            JobCategory jobCategory = JobCategory.valueOf(category.toUpperCase());
            List<JobResponse> jobs = jobService.getAllAvailableJobs()
                    .stream()
                    .filter(job -> job.getCategory() == jobCategory)
                    .toList();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Test endpoint to check if job APIs are working
    @GetMapping("/test")
    public ResponseEntity<String> testJobApi() {
        return ResponseEntity.ok("Job API is working!");
    }
}
