package com.GoldenOpportunity;

import com.GoldenOpportunity.Roles.*;
import com.GoldenOpportunity.Login.*;

import java.util.ArrayList;
import java.util.List;

// FIXME: THIS VERSION USES AN IN MEMORY USER LIST NOT A REPOSITORY CLASS YET
public class AuthenticationController {
    private final List<User> users;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    public AuthenticationController() {
        users = new ArrayList<>();

        // Sample users for testing
        users.add(new Admin(1, "admin1", "adminpass", "admin@golden.com"));
        users.add(new Clerk(2, "clerk1", "clerkpass", "clerk@golden.com"));
        users.add(new Guest(3, "guest1", "guestpass", "guest@golden.com", false));
    }

    public LoginResult logIn(String username, String password) {
        User user = findByUsername(username);

        if (user == null) {
            return new LoginResult(false, "Invalid credentials.", null);
        }

        if (!user.isActive()) {
            return new LoginResult(false, "Account is locked or disabled.", null);
        }

        if (!user.getPassword().equals(password)) {
            user.incrementFailedLoginCount();

            if (user.getFailedLoginCount() >= MAX_FAILED_ATTEMPTS) {
                user.lockAccount();
                return new LoginResult(false, "Too many failed attempts. Account locked.", null);
            }

            return new LoginResult(false, "Invalid credentials.", null);
        }

        user.resetFailedLoginCount();
        Session session = new Session(user.getUserId(), user.getRole());

        return new LoginResult(true, "Authentication successful.", session);
    }

    private User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}