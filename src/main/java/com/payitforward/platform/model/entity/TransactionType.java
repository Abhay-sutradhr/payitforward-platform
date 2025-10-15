package com.payitforward.platform.model.entity;

public enum TransactionType {
    JOB_PAYMENT,        // Payment for completed job
    JOB_REFUND,         // Refund when job is cancelled
    BONUS_PAYMENT,      // Extra payment for exceptional work
    PLATFORM_FEE,       // Platform service fee (future use)
    INITIAL_CREDITS     // Starting credits for new users
}
