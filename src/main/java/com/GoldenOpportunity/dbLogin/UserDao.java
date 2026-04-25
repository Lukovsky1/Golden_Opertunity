package com.GoldenOpportunity.dbLogin;

import com.GoldenOpportunity.User;

import java.sql.*;
import java.time.Instant;

import static com.GoldenOpportunity.Login.enums.AccountStatus.ACTIVE;

/**
 * Data Access Object focused on the {@code users} table.
 *
 * Provides CRUD operations and small domain-specific helpers for login behavior
 * (failed-attempt increments and lockouts).
 */
public class UserDao {
    /** Ensure the DB file and required tables exist. Safe to call multiple times. */
    public void initializeSchema() throws SQLException {
        Database.initialize();
    }

    /** Lookup a user by exact username. Returns null when not found. */
    public DbUser findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, account_status, failed_login_count, contact_info " +
                "FROM users WHERE username = ?";
        return findOneByField(sql, username);
    }

    /** Lookup a user by exact email/contact info. Returns null when not found. */
    public DbUser findByEmail(String email) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, account_status, failed_login_count, contact_info " +
                "FROM users WHERE contact_info = ?";
        return findOneByField(sql, email);
    }

    /** Lookup a user by their given id */
    public DbUser findById(int id) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, account_status, failed_login_count, contact_info " +
                "FROM users WHERE id = ?";
        return findOneByField(sql, String.valueOf(id));
    }

    private DbUser findOneByField(String sql, String value) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                // Map the row onto our lightweight DbUser POJO used by auth routines.
                return new DbUser(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("account_status"),
                        rs.getInt("failed_login_count"),
                        rs.getString("contact_info")
                );
            }
        }
    }

    /** Cgheused for adding product to guest's cart
     *
     * @param guestID
     * @return
     * @throws SQLException
     */
    public boolean isAuthenticated(int guestID) throws SQLException {
        try {
            DbUser guest = findById(guestID);
            if (guest == null) return false;
            if (guest.accountStatus.equals(ACTIVE)) return true;
            return false;
        } catch (SQLException e) {
            System.err.println("Error checking authentication from guest database");
            throw e;
        }
    }

    /** Create a new user with a freshly hashed password. Returns generated ID or -1 on failure. */
    public int createUser(String username, String rawPassword, String role, String contactInfo) throws SQLException {
        String now = Instant.now().toString();
        String sql = "INSERT INTO users (username, password_hash, role, account_status, failed_login_count, contact_info, created_at, updated_at) " +
                "VALUES (?, ?, ?, 'ACTIVE', 0, ?, ?, ?)";
        String hash = PasswordHasher.hash(rawPassword); // PBKDF2 hash with salt
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, role);
            ps.setString(4, contactInfo);
            ps.setString(5, now);
            ps.setString(6, now);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /** Update the password by hashing the new plaintext and writing it to the DB. */
    public void updatePassword(int userId, String newRawPassword) throws SQLException {
        String hash = PasswordHasher.hash(newRawPassword);
        String now = Instant.now().toString();
        String sql = "UPDATE users SET password_hash = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, now);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    /**
     * Increment the failed-login counter. If the counter reaches {@code maxAttempts}, lock the account.
     *
     * The single UPDATE uses a CASE expression to avoid a race between select/increment/lock.
     */
    public void incrementFailedCountAndMaybeLock(int userId, int maxAttempts) throws SQLException {
        String sql = "UPDATE users " +
                "SET failed_login_count = failed_login_count + 1, " +
                "    account_status = CASE WHEN failed_login_count + 1 >= ? THEN 'LOCKED' ELSE account_status END, " +
                "    updated_at = ? " +
                "WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maxAttempts);
            ps.setString(2, Instant.now().toString());
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    /** Reset the failure counter after a successful login. */
    public void resetFailedCount(int userId) throws SQLException {
        String sql = "UPDATE users SET failed_login_count = 0, updated_at = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Instant.now().toString());
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /** Convenience predicate for checking an account's active status. */
    public boolean isActive(DbUser user) {
        return user != null && "ACTIVE".equalsIgnoreCase(user.accountStatus);
    }

    // ---- Helpers used by tests/demo tooling ----

    /** Set account back to ACTIVE and zero failed attempt counter for the named user. */
    public void resetAccountState(String username) throws SQLException {
        String sql = "UPDATE users SET failed_login_count = 0, account_status = 'ACTIVE', updated_at = ? WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Instant.now().toString());
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    /** Inspect the current failed attempt count for a user. Returns -1 when user not found. */
    public int getFailedCount(String username) throws SQLException {
        String sql = "SELECT failed_login_count FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return -1;
            }
        }
    }

    /** Inspect textual account status (e.g., ACTIVE or LOCKED). Returns null when user not found. */
    public String getStatus(String username) throws SQLException {
        String sql = "SELECT account_status FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
                return null;
            }
        }
    }
}
