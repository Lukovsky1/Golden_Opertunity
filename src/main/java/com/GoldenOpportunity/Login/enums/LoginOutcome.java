package com.GoldenOpportunity.Login.enums;

/**
 * Outcome of a login attempt.
 */
public enum LoginOutcome {
    /** Credentials verified and session created. */
    SUCCESS,
    /** Authentication failed due to invalid credentials or state. */
    FAILURE
}
