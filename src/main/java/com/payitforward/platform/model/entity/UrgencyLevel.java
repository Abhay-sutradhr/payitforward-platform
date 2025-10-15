package com.payitforward.platform.model.entity;

public enum UrgencyLevel {
    LOW,      // Can wait weeks
    MEDIUM,   // Can wait days  
    HIGH,     // Needed within 24-48 hours
    URGENT    // Needed ASAP (same day)
}
