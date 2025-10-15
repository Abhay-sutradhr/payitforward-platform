package com.payitforward.platform.repository;

import com.payitforward.platform.model.entity.Job;
import com.payitforward.platform.model.entity.JobCategory;
import com.payitforward.platform.model.entity.JobStatus;
import com.payitforward.platform.model.entity.UrgencyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    // Find jobs by status
    List<Job> findByStatus(JobStatus status);
    List<Job> findByStatusOrderByCreatedAtDesc(JobStatus status);
    
    // Find jobs by poster
    List<Job> findByPosterId(Long posterId);
    List<Job> findByPosterIdOrderByCreatedAtDesc(Long posterId);
    
    // Find assigned jobs
    List<Job> findByAssignedToUserId(Long userId);
    List<Job> findByAssignedToUserIdAndStatus(Long userId, JobStatus status);
    
    // Find available jobs (open status)
    @Query("SELECT j FROM Job j WHERE j.status = 'OPEN' ORDER BY j.urgencyLevel DESC, j.createdAt DESC")
    List<Job> findAvailableJobs();
    
    // Find jobs by category
    List<Job> findByCategoryAndStatus(JobCategory category, JobStatus status);
    
    // Find jobs by credit range
    List<Job> findByCreditCostBetweenAndStatus(BigDecimal minCredits, BigDecimal maxCredits, JobStatus status);
    List<Job> findByCreditCostLessThanEqualAndStatus(BigDecimal maxCredits, JobStatus status);
    
    // Find urgent jobs
    List<Job> findByUrgencyLevelAndStatusOrderByCreatedAtDesc(UrgencyLevel urgency, JobStatus status);
    
    // Find jobs by location (for local help)
    List<Job> findByLocationContainingAndStatusOrderByCreatedAtDesc(String location, JobStatus status);
    
    // Find remote jobs
    List<Job> findByIsRemoteAndStatusOrderByCreatedAtDesc(Boolean isRemote, JobStatus status);
    
    // Find jobs with skills matching
    @Query("SELECT j FROM Job j WHERE j.requiredSkills LIKE %:skill% AND j.status = :status")
    List<Job> findBySkillAndStatus(@Param("skill") String skill, @Param("status") JobStatus status);
    
    // Find expiring jobs (deadline approaching)
    @Query("SELECT j FROM Job j WHERE j.deadline <= :deadline AND j.status = 'OPEN'")
    List<Job> findExpiringJobs(@Param("deadline") LocalDateTime deadline);
    
    // Count jobs by status for dashboard
    long countByStatus(JobStatus status);
    long countByPosterId(Long posterId);
    long countByAssignedToUserId(Long userId);
}
