package com.payitforward.platform.model.entity;

public enum JobStatus {
    OPEN,        // Available for applications
    ASSIGNED,    // Helper selected, work starting
    IN_PROGRESS, // Work is being done
    COMPLETED,   // Work finished, credits transferred
    CANCELLED    // Job cancelled by poster
}
