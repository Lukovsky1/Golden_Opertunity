package com.GoldenOpportunity.Login;

/**
 * Generic result object for authentication-related actions that do not issue a session.
 */
public class AuthResult {
    private final boolean success;
    private final String message;

    public AuthResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
