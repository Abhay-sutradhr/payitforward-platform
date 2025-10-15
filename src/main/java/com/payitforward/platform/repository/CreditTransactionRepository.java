package com.payitforward.platform.repository;

import com.payitforward.platform.model.entity.CreditTransaction;
import com.payitforward.platform.model.entity.TransactionStatus;
import com.payitforward.platform.model.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Long> {
    
    // Find transactions by user (sent or received)
    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.fromUserId = :userId OR ct.toUserId = :userId ORDER BY ct.createdAt DESC")
    List<CreditTransaction> findByUserId(@Param("userId") Long userId);
    
    // Find transactions sent by user
    List<CreditTransaction> findByFromUserIdOrderByCreatedAtDesc(Long fromUserId);
    
    // Find transactions received by user
    List<CreditTransaction> findByToUserIdOrderByCreatedAtDesc(Long toUserId);
    
    // Find transactions by job
    List<CreditTransaction> findByJobId(Long jobId);
    Optional<CreditTransaction> findByJobIdAndTransactionType(Long jobId, TransactionType type);
    
    // Find transactions by status
    List<CreditTransaction> findByStatus(TransactionStatus status);
    List<CreditTransaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status);
    
    // Find transactions by type
    List<CreditTransaction> findByTransactionType(TransactionType type);
    
    // Calculate total sent by user
    @Query("SELECT COALESCE(SUM(ct.amount), 0) FROM CreditTransaction ct WHERE ct.fromUserId = :userId AND ct.status = 'COMPLETED'")
    BigDecimal getTotalSentByUser(@Param("userId") Long userId);
    
    // Calculate total received by user
    @Query("SELECT COALESCE(SUM(ct.amount), 0) FROM CreditTransaction ct WHERE ct.toUserId = :userId AND ct.status = 'COMPLETED'")
    BigDecimal getTotalReceivedByUser(@Param("userId") Long userId);
    
    // Get transaction history for a user within date range
    @Query("SELECT ct FROM CreditTransaction ct WHERE (ct.fromUserId = :userId OR ct.toUserId = :userId) " +
           "AND ct.createdAt BETWEEN :startDate AND :endDate ORDER BY ct.createdAt DESC")
    List<CreditTransaction> findUserTransactionsBetweenDates(@Param("userId") Long userId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    // Get recent transactions (for admin dashboard)
    @Query("SELECT ct FROM CreditTransaction ct ORDER BY ct.createdAt DESC")
    List<CreditTransaction> findRecentTransactions();
    
    // Count transactions by status
    long countByStatus(TransactionStatus status);
    long countByTransactionType(TransactionType type);
}
