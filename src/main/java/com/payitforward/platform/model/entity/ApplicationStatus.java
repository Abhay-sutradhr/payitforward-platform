package com.payitforward.platform.model.entity;

public enum ApplicationStatus {
    PENDING,    // Application submitted, awaiting review
    ACCEPTED,   // Application accepted, user assigned to job
    REJECTED,   // Application rejected by job poster
    WITHDRAWN   // Application withdrawn by applicant
}
