package com.payitforward.platform.controller;

import com.payitforward.platform.model.dto.CreditTransactionResponse;
import com.payitforward.platform.service.CreditTransactionService;
import com.payitforward.platform.service.UserService;
import com.payitforward.platform.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/credits")
@CrossOrigin(origins = "*")
public class CreditHistoryController {
    
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
    
    @GetMapping("/history")
    public ResponseEntity<?> getCreditHistory(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("Authentication required");
            }
            
            List<CreditTransactionResponse> history = transactionService
                    .getUserTransactionHistory(userId);
            
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch credit history: " + e.getMessage());
        }
    }
    
    @GetMapping("/summary")
    public ResponseEntity<?> getCreditSummary(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body("Authentication required");
            }
            
            BigDecimal totalEarned = transactionService.getUserTotalEarnings(userId);
            BigDecimal totalSpent = transactionService.getUserTotalSpent(userId);
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalEarned", totalEarned);
            summary.put("totalSpent", totalSpent);
            summary.put("netGain", totalEarned.subtract(totalSpent));
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch credit summary: " + e.getMessage());
        }
    }
    
    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<String> testCreditsApi() {
        return ResponseEntity.ok("Credits API is working!");
    }
}
