package com.payitforward.platform.repository;

import com.payitforward.platform.model.entity.User;
import com.payitforward.platform.model.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Basic finder methods (Spring Data JPA will auto-implement these)
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    // Find users by status
    List<User> findByAccountStatus(AccountStatus status);
    List<User> findByAccountStatusNot(AccountStatus status);
    
    // Find users by credit range
    List<User> findByTotalCreditsGreaterThan(BigDecimal credits);
    List<User> findByTotalCreditsLessThan(BigDecimal credits);
    List<User> findByTotalCreditsBetween(BigDecimal minCredits, BigDecimal maxCredits);
    
    // Find users by rating
    List<User> findByAverageRatingGreaterThanEqual(BigDecimal rating);
    
    // Find active users with experience
    List<User> findByAccountStatusAndTotalJobsCompletedGreaterThan(AccountStatus status, Integer jobsCompleted);
    
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    // Custom query to find top users by rating
    @Query("SELECT u FROM User u WHERE u.accountStatus = :status ORDER BY u.averageRating DESC, u.totalJobsCompleted DESC")
    List<User> findTopUsersByRating(@Param("status") AccountStatus status);
    
    // Custom query to find users with low credits (might need help)
    @Query("SELECT u FROM User u WHERE u.totalCredits < :creditThreshold AND u.accountStatus = 'ACTIVE'")
    List<User> findUsersNeedingHelp(@Param("creditThreshold") BigDecimal creditThreshold);
    
    // Check if username or email already exists
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameOrEmail(String username, String email);
    
    // Count users by status
    long countByAccountStatus(AccountStatus status);
}
