package com.GoldenOpportunity.Login;

import com.GoldenOpportunity.Login.enums.LoginOutcome;
import com.GoldenOpportunity.Login.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an authenticated user session.
 *
 * <p>Each session receives a random UUID, tracks the user ID and role,
 * stores the creation timestamp, and records the login outcome.</p>
 */
public class Session {
    private final String sessionId;
    private final int userId;
    private final Role role;
    private final LocalDateTime createdAt;
    private final LoginOutcome status;

    /**
     * Creates a successful session for a given user and role.
     *
     * @param userId the authenticated user's unique ID
     * @param role the authenticated user's role
     */
    public Session(int userId, Role role) {
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.status = LoginOutcome.SUCCESS;
    }

    /**
     * @return generated unique session identifier
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @return the authenticated user's ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the authenticated user's role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @return timestamp when the session was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @return outcome status of the login (always SUCCESS for this constructor)
     */
    public LoginOutcome getStatus() {
        return status;
    }
}
