package com.GoldenOpportunity.Login.enums;

/**
 * Lifecycle status of a user account used to control authentication.
 */
public enum AccountStatus {
    /** Account is active and can authenticate. */
    ACTIVE,
    /** Account is temporarily locked due to policy (e.g., failed attempts). */
    LOCKED,
    /** Account is permanently disabled by an administrator. */
    DISABLED
}
