package com.payitforward.platform.controller;

import com.payitforward.platform.model.dto.ApplyToJobRequest;
import com.payitforward.platform.model.dto.JobApplicationResponse;
import com.payitforward.platform.model.entity.JobApplication;
import com.payitforward.platform.service.JobApplicationService;
import com.payitforward.platform.service.UserService;
import com.payitforward.platform.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class JobApplicationController {
    
    @Autowired
    private JobApplicationService applicationService;
    
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
    
    // Apply to a job
    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> applyToJob(@PathVariable Long jobId,
                                       @Valid @RequestBody ApplyToJobRequest request,
                                       HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Authentication required"));
            }
            
            JobApplication application = applicationService.applyToJob(jobId, userId, request);
            JobApplicationResponse response = applicationService.convertToApplicationResponse(application);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Application failed: " + e.getMessage()));
        }
    }
    
    // Accept an application
    @PostMapping("/accept/{applicationId}")
    public ResponseEntity<?> acceptApplication(@PathVariable Long applicationId,
                                             HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Authentication required"));
            }
            
            JobApplication application = applicationService.acceptApplication(applicationId, userId);
            JobApplicationResponse response = applicationService.convertToApplicationResponse(application);
            
            return ResponseEntity.ok(Map.of(
                "message", "Application accepted successfully",
                "application", response
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Accept failed: " + e.getMessage()));
        }
    }
    
    // Reject an application (job poster only)
    @PostMapping("/reject/{applicationId}")
    public ResponseEntity<?> rejectApplication(@PathVariable Long applicationId,
                                             HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Authentication required"));
            }
            
            JobApplication application = applicationService.rejectApplication(applicationId, userId);
            JobApplicationResponse response = applicationService.convertToApplicationResponse(application);
            
            return ResponseEntity.ok(Map.of(
                "message", "Application rejected successfully",
                "application", response
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Reject failed: " + e.getMessage()));
        }
    }
    
    // Withdraw application (applicant only)
    @PutMapping("/withdraw/{applicationId}")
    public ResponseEntity<?> withdrawApplication(@PathVariable Long applicationId,
                                               HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Authentication required"));
            }
            
            JobApplication application = applicationService.withdrawApplication(applicationId, userId);
            JobApplicationResponse response = applicationService.convertToApplicationResponse(application);
            
            return ResponseEntity.ok(Map.of(
                "message", "Application withdrawn successfully",
                "application", response
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Withdraw failed: " + e.getMessage()));
        }
    }
    
    // Get applications for a specific job (job poster only)
    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable Long jobId,
                                                  HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Authentication required"));
            }
            
            List<JobApplicationResponse> applications = 
                applicationService.getApplicationsForJob(jobId, userId);
            
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to fetch applications: " + e.getMessage()));
        }
    }
    
    // Get current user's applications
    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Authentication required"));
            }
            
            List<JobApplicationResponse> applications = 
                applicationService.getMyApplications(userId);
            
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to fetch applications: " + e.getMessage()));
        }
    }
    
    // Get application details by ID
    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long applicationId,
                                               HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Authentication required"));
            }
            
            JobApplicationResponse application = 
                applicationService.getApplicationById(applicationId, userId);
            
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to fetch application: " + e.getMessage()));
        }
    }
    
    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<?> testApplicationApi() {
        return ResponseEntity.ok(Map.of(
            "message", "Job Application API is working!",
            "timestamp", System.currentTimeMillis(),
            "endpoints", List.of(
                "POST /api/applications/apply/{jobId}",
                "POST /api/applications/accept/{applicationId}",
                "POST /api/applications/reject/{applicationId}",
                "PUT /api/applications/withdraw/{applicationId}",
                "GET /api/applications/job/{jobId}",
                "GET /api/applications/my-applications",
                "GET /api/applications/{applicationId}",
                "GET /api/applications/test"
            )
        ));
    }
}
