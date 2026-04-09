package com.GoldenOpportunity.Login;

/**
 * Immutable result object describing the outcome of a login attempt.
 * Contains a success flag, a human-readable message, and an optional session.
 */
public class LoginResult {
    private final boolean success;
    private final String message;
    private final Session session;

    /**
     * Constructs a new login result.
     *
     * @param success whether authentication succeeded
     * @param message explanatory message for UI/logging
     * @param session a created {@link Session} when success is true; otherwise null
     */
    public LoginResult(boolean success, String message, Session session) {
        this.success = success;
        this.message = message;
        this.session = session;
    }

    /**
     * @return true if authentication succeeded; false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return message describing the outcome
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the associated session when successful, otherwise null
     */
    public Session getSession() {
        return session;
    }
}
