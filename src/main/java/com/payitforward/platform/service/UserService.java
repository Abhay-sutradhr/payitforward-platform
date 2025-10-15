package com.payitforward.platform.service;

import com.payitforward.platform.model.dto.ChangePasswordRequest;
import com.payitforward.platform.model.dto.RegisterRequest;
import com.payitforward.platform.model.dto.UpdateProfileRequest;
import com.payitforward.platform.model.dto.UserProfileResponse;
import com.payitforward.platform.model.dto.UserResponse;
import com.payitforward.platform.model.entity.AccountStatus;
import com.payitforward.platform.model.entity.User;
import com.payitforward.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User createUser(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setTotalCredits(new BigDecimal("10.00")); // Starting credits
        user.setAverageRating(BigDecimal.ZERO);
        user.setTotalJobsCompleted(0);
        user.setTotalJobsPosted(0);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }
    
    public UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setTotalCredits(user.getTotalCredits());
        response.setAverageRating(user.getAverageRating());
        response.setTotalJobsCompleted(user.getTotalJobsCompleted());
        response.setTotalJobsPosted(user.getTotalJobsPosted());
        response.setAccountStatus(user.getAccountStatus());
        response.setCreatedAt(user.getCreatedAt());
        
        return response;
    }
    
 // Add these methods to your existing UserService class

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new UserProfileResponse(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getPhone(),
            user.getAddress(),
            user.getTotalCredits(),
            user.getTotalJobsCompleted(),
            user.getTotalJobsPosted(),
            user.getAverageRating(),
            user.getCreatedAt()
        );
    }

    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Set new password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }
 
}
