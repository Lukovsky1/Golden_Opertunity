package com.GoldenOpportunity.dbLogin;

import com.GoldenOpportunity.Login.AuthResult;
import com.GoldenOpportunity.Login.LoginResult;
import com.GoldenOpportunity.Login.Session;
import com.GoldenOpportunity.Login.enums.Role;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;

/**
 * Concrete authentication service that uses the {@code users} table for verification.
 *
 * Flow:
 * 1) Look up the user by username, then by email if username lookup fails.
 * 2) Reject if not found or not ACTIVE.
 * 3) Verify password using PBKDF2 (see {@link PasswordHasher}).
 *    - On mismatch: increment failed counter and lock after N attempts.
 *    - On match: reset failed counter and return a {@link Session} with the user's role.
 */
public class DbAuthenticationService {
    /** Number of consecutive failures before the account is locked. */
    private static final int MAX_FAILED_ATTEMPTS = 3;

    private final UserDao userDao = new UserDao();

    public DbAuthenticationService() {
        try {
            // Ensure DB and schema are present before handling logins.
            userDao.initializeSchema();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    /** Attempt to authenticate a user and return a UI-friendly result object. */
    public LoginResult logIn(String username, String password) {
        String normalizedUsername = username == null ? "" : username.trim().toLowerCase();

        if (EmailValidator.looksLikeEmail(normalizedUsername) && !EmailValidator.isValidEmail(normalizedUsername)) {
            return new LoginResult(false, EmailValidator.supportedDomainsMessage(), null);
        }

        try {
            // 1) Fetch user by username first, then fall back to email/contact info.
            DbUser user = userDao.findByUsername(username);
            if (user == null) {
                user = userDao.findByEmail(normalizedUsername);
            }
            if (user == null) {
                // Do not reveal whether the username exists.
                return new LoginResult(false, "Invalid credentials.", null);
            }

            // 2) Check account state
            if (!userDao.isActive(user)) {
                return new LoginResult(false, "Account is locked or disabled.", null);
            }

            // 3) Verify password
            if (!PasswordHasher.verify(password, user.passwordHash)) {
                // Count the failed attempt and possibly transition the account to LOCKED.
                userDao.incrementFailedCountAndMaybeLock(user.id, MAX_FAILED_ATTEMPTS);
                return new LoginResult(false, "Invalid credentials.", null);
            }

            // Success path: reset counter and issue a new session with role information.
            userDao.resetFailedCount(user.id);
            Role role = Role.valueOf(user.role.toUpperCase());
            Session session = new Session(user.id, role);
            return new LoginResult(true, "Authentication successful.", session);
        } catch (SQLException e) {
            // Avoid leaking details; surface a generic error to the UI.
            return new LoginResult(false, "Authentication error.", null);
        }
    }

    public AuthResult signUp(String username, String email, String password, String fullName, String phoneNumber) {
        return createUserWithRole(
                username,
                email,
                password,
                fullName,
                phoneNumber,
                "GUEST",
                "Account created successfully. Please log in."
        );
    }

    public AuthResult createPrivilegedUser(String username, String email, String password,
                                           String fullName, String phoneNumber, String role) {
        String normalizedRole = role == null ? "" : role.trim().toUpperCase();
        if (!"ADMIN".equals(normalizedRole) && !"CLERK".equals(normalizedRole)) {
            return new AuthResult(false, "Only ADMIN and CLERK accounts can be created from the admin page.");
        }

        return createUserWithRole(
                username,
                email,
                password,
                fullName,
                phoneNumber,
                normalizedRole,
                normalizedRole + " account created successfully."
        );
    }

    private AuthResult createUserWithRole(String username, String email, String password,
                                          String fullName, String phoneNumber,
                                          String role, String successMessage) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        String normalizedFullName = fullName == null ? "" : fullName.trim();
        String normalizedPhoneNumber = phoneNumber == null ? "" : phoneNumber.trim();

        try {
            if (normalizedUsername.isEmpty() || normalizedEmail.isEmpty() || normalizedFullName.isEmpty()
                    || normalizedPhoneNumber.isEmpty() || password == null || password.isBlank()) {
                return new AuthResult(false, "All fields are required.");
            }

            if (!EmailValidator.isValidEmail(normalizedEmail)) {
                return new AuthResult(false, EmailValidator.supportedDomainsMessage());
            }

            if (userDao.findByUsername(normalizedUsername) != null) {
                return new AuthResult(false, "That username is already taken.");
            }
            if (userDao.findByEmail(normalizedEmail) != null) {
                return new AuthResult(false, "That email is already in use.");
            }

            int userId = userDao.createUser(
                    normalizedUsername,
                    password,
                    role,
                    normalizedEmail,
                    normalizedFullName,
                    normalizedPhoneNumber
            );
            if (userId < 0) {
                return new AuthResult(false, "Unable to create account.");
            }

            return new AuthResult(true, successMessage);
        } catch (SQLIntegrityConstraintViolationException e) {
            return new AuthResult(false, "That username or email is already in use.");
        } catch (SQLException e) {
            return new AuthResult(false, "Authentication error.");
        }
    }
}
