package com.GoldenOpportunity;

import com.GoldenOpportunity.Roles.*;
import com.GoldenOpportunity.Login.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles user authentication responsibilities for the application.
 *
 * Note: This implementation uses an in-memory list of sample users
 * for demonstration/testing only. There is no persistence layer yet.
 */
public class AuthenticationController {
    private final List<User> users;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    /**
     * Creates the controller and seeds an in-memory list of test users.
     */
    public AuthenticationController() {
        users = new ArrayList<>();

        // Seed sample users for manual testing (no DB yet)
        users.add(new Admin(1, "admin1", "adminpass", "admin@golden.com"));
        users.add(new Clerk(2, "clerk1", "clerkpass", "clerk@golden.com"));
        users.add(new Guest(3, "guest1", "guestpass", "guest@golden.com",
                false, new ArrayList<>()));
    }

    /**
     * Attempts to authenticate a user by username and password.
     *
     * Behavior:
     * - If the user is not found or password is incorrect: return failure.
     * - If the account is not active: return failure.
     * - On incorrect password: increment failed attempts and lock after 3 tries.
     * - On success: reset failed attempts and create a new session.</p>
     *
     * @param username the username to look up
     * @param password the plaintext password to validate
     * @return a {@link LoginResult} indicating success/failure and a session on success
     */
    public LoginResult logIn(String username, String password) {
        // Lookup user by username (in-memory)
        User user = findByUsername(username);

        if (user == null) {
            // Do not reveal whether username or password was wrong
            return new LoginResult(false, "Invalid credentials.", null);
        }

        if (!user.isActive()) {
            // Locked or disabled accounts cannot log in
            return new LoginResult(false, "Account is locked or disabled.", null);
        }

        if (!user.getPassword().equals(password)) {
            // Bad password: increment counter and lock if threshold reached
            user.incrementFailedLoginCount();

            if (user.getFailedLoginCount() >= MAX_FAILED_ATTEMPTS) {
                user.lockAccount();
                return new LoginResult(false, "Too many failed attempts. Account locked.", null);
            }

            return new LoginResult(false, "Invalid credentials.", null);
        }

        // Successful authentication: reset counters and establish a session
        user.resetFailedLoginCount();
        Session session = new Session(user.getUserId(), user.getRole());

        return new LoginResult(true, "Authentication successful.", session);
    }

    /**
     * Finds a user by an exact username match within the in-memory list.
     *
     * @param username the username to search for
     * @return the matching {@link User} or {@code null} if not found
     */
    private User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
