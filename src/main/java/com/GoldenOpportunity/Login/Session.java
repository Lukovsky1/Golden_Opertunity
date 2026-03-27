package com.GoldenOpportunity.Login;

import com.GoldenOpportunity.Login.enums.LoginOutcome;
import com.GoldenOpportunity.Login.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
    private final String sessionId;
    private final int userId;
    private final Role role;
    private final LocalDateTime createdAt;
    private final LoginOutcome status;

    public Session(int userId, Role role) {
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.status = LoginOutcome.SUCCESS;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LoginOutcome getStatus() {
        return status;
    }
}