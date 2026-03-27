package com.GoldenOpportunity.Login;

public class LoginResult {
    private final boolean success;
    private final String message;
    private final Session session;

    public LoginResult(boolean success, String message, Session session) {
        this.success = success;
        this.message = message;
        this.session = session;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Session getSession() {
        return session;
    }
}