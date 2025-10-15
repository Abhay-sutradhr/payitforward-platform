package com.payitforward.platform.model.entity;

public enum TransactionStatus {
    PENDING,     // Transaction initiated but not completed
    COMPLETED,   // Transaction successfully completed
    FAILED,      // Transaction failed
    CANCELLED    // Transaction cancelled
}
