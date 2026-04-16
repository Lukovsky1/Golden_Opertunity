package com.GoldenOpportunity.dbLogin;

import com.GoldenOpportunity.Login.LoginResult;
import com.GoldenOpportunity.Login.Session;
import com.GoldenOpportunity.Login.enums.Role;

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
        try {
            // 1) Fetch user by username first, then fall back to email/contact info.
            DbUser user = userDao.findByUsername(username);
            if (user == null) {
                user = userDao.findByEmail(username);
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
}
