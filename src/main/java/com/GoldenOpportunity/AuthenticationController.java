package com.GoldenOpportunity;

import com.GoldenOpportunity.Login.AuthResult;
import com.GoldenOpportunity.Login.LoginResult;
import com.GoldenOpportunity.dbLogin.DbAuthenticationService;

/**
 * Thin façade between UI and the database-backed authentication service.
 *
 * Purpose:
 * - Give UI code a stable, intention-revealing API (e.g., {@link #logIn(String, String)}),
 *   while allowing the underlying auth mechanism to change without touching the UI.
 * - Centralize cross-cutting concerns later (logging, metrics, rate limiting) if needed.
 */
public class AuthenticationController {
    /** Concrete service that talks to SQLite through the DAO layer. */
    private final DbAuthenticationService authService;

    /** Construct the controller and ensure the auth service is initialized. */
    public AuthenticationController() {
        this.authService = new DbAuthenticationService();
    }

    /**
     * Attempt to authenticate a user by username and password.
     * The returned {@link LoginResult} is designed to be consumed by UI.
     */
    public LoginResult logIn(String username, String password) {
        return authService.logIn(username, password);
    }

    public AuthResult signUp(String username, String email, String password, String fullName, String phoneNumber) {
        return authService.signUp(username, email, password, fullName, phoneNumber);
    }

    public AuthResult createPrivilegedUser(String username, String email, String password,
                                           String fullName, String phoneNumber, String role) {
        return authService.createPrivilegedUser(username, email, password, fullName, phoneNumber, role);
    }
    */
}
