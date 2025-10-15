package com.payitforward.platform.repository;

import com.payitforward.platform.model.entity.JobApplication;
import com.payitforward.platform.model.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    // Find applications by job
    List<JobApplication> findByJobId(Long jobId);
    List<JobApplication> findByJobIdOrderByAppliedAtDesc(Long jobId);
    
    // Find applications by applicant
    List<JobApplication> findByApplicantId(Long applicantId);
    List<JobApplication> findByApplicantIdOrderByAppliedAtDesc(Long applicantId);
    
    // Find applications by status
    List<JobApplication> findByStatus(ApplicationStatus status);
    List<JobApplication> findByJobIdAndStatus(Long jobId, ApplicationStatus status);
    List<JobApplication> findByApplicantIdAndStatus(Long applicantId, ApplicationStatus status);
    
    // Check if user already applied to job
    Optional<JobApplication> findByJobIdAndApplicantId(Long jobId, Long applicantId);
    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
    
    // Count applications
    long countByJobId(Long jobId);
    long countByJobIdAndStatus(Long jobId, ApplicationStatus status);
    long countByApplicantId(Long applicantId);
    
    // Find accepted application for a job (should be only one) - RENAMED METHOD
    Optional<JobApplication> findAcceptedApplicationByJobId(Long jobId);
    
    // Custom query to find accepted application for a job
    @Query("SELECT ja FROM JobApplication ja WHERE ja.jobId = :jobId AND ja.status = 'ACCEPTED'")
    Optional<JobApplication> findAcceptedApplication(@Param("jobId") Long jobId);
    
    // Get applications for jobs posted by specific user
    @Query("SELECT ja FROM JobApplication ja JOIN Job j ON ja.jobId = j.jobId WHERE j.posterId = :posterId")
    List<JobApplication> findApplicationsForJobsPostedBy(@Param("posterId") Long posterId);
    
    // Get recent applications
    @Query("SELECT ja FROM JobApplication ja ORDER BY ja.appliedAt DESC")
    List<JobApplication> findRecentApplications();
}
