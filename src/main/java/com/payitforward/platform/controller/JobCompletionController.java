package com.payitforward.platform.controller;

import com.payitforward.platform.model.dto.CompleteJobRequest;
import com.payitforward.platform.model.dto.CreditTransactionResponse;
import com.payitforward.platform.model.entity.CreditTransaction;
import com.payitforward.platform.service.CreditTransactionService;
import com.payitforward.platform.service.UserService;
import com.payitforward.platform.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobCompletionController {
    
    @Autowired
    private CreditTransactionService transactionService;
    
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
    
    @PostMapping("/complete/{jobId}")
    public ResponseEntity<?> completeJob(@PathVariable Long jobId,
                                        @Valid @RequestBody CompleteJobRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body("Authentication required");
            }
            
            CreditTransaction transaction = transactionService
                    .completeJobAndTransferCredits(jobId, userId, request);
            CreditTransactionResponse response = transactionService
                    .convertToTransactionResponse(transaction);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Job completion failed: " + e.getMessage());
        }
    }
    
    @PostMapping("/cancel/{jobId}")
    public ResponseEntity<?> cancelJob(@PathVariable Long jobId,
                                      @RequestParam String reason,
                                      HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("Authentication required");
            }
            
            CreditTransaction transaction = transactionService
                    .cancelJobAndRefund(jobId, userId, reason);
            CreditTransactionResponse response = transactionService
                    .convertToTransactionResponse(transaction);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Job cancellation failed: " + e.getMessage());
        }
    }
}
