package com.itemrecovery.model;

/**
 * Enum representing the status of lost or found items.
 * LOST: Item has been reported as lost
 * FOUND: Item has been reported as found
 * MATCHED: A potential match has been identified
 * RECOVERED: Item has been successfully recovered
 */
public enum ItemStatus {
    LOST,
    FOUND,
    MATCHED,
    RECOVERED
}
