package com.payitforward.platform.controller;

import com.payitforward.platform.model.dto.*;
import com.payitforward.platform.model.entity.User;
import com.payitforward.platform.security.JwtUtil;
import com.payitforward.platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.createUser(request);
            String token = jwtUtil.generateToken(user.getUsername());
            
            AuthResponse response = new AuthResponse(token, user.getUsername(), user.getEmail(), 
                    "User registered successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Add this to see the exact error in console
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
            );
            
            User user = userService.findByUsernameOrEmail(request.getUsernameOrEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String token = jwtUtil.generateToken(user.getUsername());
            
            AuthResponse response = new AuthResponse(token, user.getUsername(), user.getEmail(), 
                    "Login successful");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            UserProfileResponse profile = userService.getUserProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to get profile: " + e.getMessage()));
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            userService.updateProfile(userId, dto);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to update profile: " + e.getMessage()));
        }
    }
    
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            userService.changePassword(userId, dto);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to change password: " + e.getMessage()));
        }
    }
    
    private Long getUserIdFromToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String usernameOrEmail = jwtUtil.getUsernameFromToken(token);
            if (usernameOrEmail != null) {
                User user = userService.findByUsernameOrEmail(usernameOrEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                return user.getUserId();
            }
        }
        throw new RuntimeException("Invalid or missing token");
    }
}
