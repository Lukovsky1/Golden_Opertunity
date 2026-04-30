package com.GoldenOpportunity.dbLogin;

/**
 * Immutable projection of a user row used by the authentication layer.
 *
 * This keeps only what the auth flow needs and avoids coupling UI to JDBC.
 */
public class DbUser {
    /** Primary key in the users table. */
    public final int id;
    /** Unique username for login. */
    public final String username;
    /** PBKDF2-encoded password string (scheme$iterations$salt$hash). */
    public final String passwordHash;
    /** Role string as stored (e.g., "ADMIN", "CLERK", "GUEST"). */
    public final String role;
    /** Account status (e.g., ACTIVE, LOCKED). */
    public final String accountStatus;
    /** Count of consecutive failed login attempts. */
    public final int failedLoginCount;
    /** Optional contact information for the user. */
    public final String contactInfo;
    /** Full display name for the user. */
    public final String fullName;
    /** Phone number for the user. */
    public final String phoneNumber;

    public DbUser(int id, String username, String passwordHash, String role,
                  String accountStatus, int failedLoginCount, String contactInfo,
                  String fullName, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.accountStatus = accountStatus;
        this.failedLoginCount = failedLoginCount;
        this.contactInfo = contactInfo;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }
}
