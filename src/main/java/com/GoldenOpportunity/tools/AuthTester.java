package com.GoldenOpportunity.tools;

import com.GoldenOpportunity.AuthenticationController;
import com.GoldenOpportunity.Login.AuthResult;
import com.GoldenOpportunity.Login.LoginResult;
import com.GoldenOpportunity.dbLogin.DbUser;
import com.GoldenOpportunity.dbLogin.UserDao;

/**
 * Simple console tester for the DB and AuthenticationController.
 * Exits with non-zero code on failure; prints PASS/FAIL per step.
 */
public class AuthTester {
    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        var dao = new UserDao();
        dao.initializeSchema();

        // Ensure seed users
        seedIfMissing(dao, "admin1", "adminpass", "ADMIN", "admin@golden.com", "Admin One", "555-0101");
        seedIfMissing(dao, "clerk1", "clerkpass", "CLERK", "clerk@golden.com", "Clerk One", "555-0102");
        seedIfMissing(dao, "guest1", "guestpass", "GUEST", "guest@golden.com", "Guest One", "555-0103");

        // Use guest1 for stateful tests
        dao.resetAccountState("guest1");

        var auth = new AuthenticationController();

        // 1) Successful login
        check("success login (guest1)", () -> {
            var res = auth.logIn("guest1", "guestpass");
            assertTrue(res.isSuccess(), "Expected success");
            assertEquals(0, dao.getFailedCount("guest1"), "failed count reset");
        });

        check("success login by supported email", () -> {
            var res = auth.logIn("guest@golden.com", "guestpass");
            assertTrue(res.isSuccess(), "Expected success");
        });

        // 2) Single wrong password increments failed count
        dao.resetAccountState("guest1");
        check("wrong password increments count", () -> {
            var res = auth.logIn("guest1", "WRONG");
            assertFalse(res.isSuccess(), "Expected failure");
            assertEquals(1, dao.getFailedCount("guest1"), "failed count = 1");
            assertEquals("ACTIVE", dao.getStatus("guest1"), "status ACTIVE");
        });

        // 3) Lock after 3 failed attempts
        dao.resetAccountState("guest1");
        check("lock after 3 failures", () -> {
            auth.logIn("guest1", "bad1");
            auth.logIn("guest1", "bad2");
            var third = auth.logIn("guest1", "bad3");
            assertFalse(third.isSuccess(), "Third failure returns failure");
            assertEquals("LOCKED", dao.getStatus("guest1"), "status LOCKED");
        });

        // 4) Locked account cannot login even with correct password
        check("locked cannot login", () -> {
            var res = auth.logIn("guest1", "guestpass");
            assertFalse(res.isSuccess(), "Locked should fail");
        });

        // 5) Reset and ensure success works again
        dao.resetAccountState("guest1");
        check("after reset, success again", () -> {
            var res = auth.logIn("guest1", "guestpass");
            assertTrue(res.isSuccess(), "Expected success after reset");
        });

        // 6) Non-existing user
        check("non-existing user fails", () -> {
            var res = auth.logIn("nope_user", "whatever");
            assertFalse(res.isSuccess(), "Expected failure for unknown user");
        });

        check("unsupported email domain blocked on login", () -> {
            var res = auth.logIn("guest@example.com", "guestpass");
            assertFalse(res.isSuccess(), "Expected failure for unsupported email domain");
        });

        // 7) Signup writes a new guest user to SQLite
        check("signup persists user in database", () -> {
            String username = "signup_test_" + System.currentTimeMillis();
            String email = username + "@gmail.com";

            String fullName = "Signup Test User";
            String phoneNumber = "555-0199";

            AuthResult res = auth.signUp(username, email, "secret123", fullName, phoneNumber);
            assertTrue(res.isSuccess(), "Expected signup success");

            DbUser createdUser = dao.findByUsername(username);
            assertTrue(createdUser != null, "Expected created user row");
            assertEquals("GUEST", createdUser.role, "role stored");
            assertEquals("ACTIVE", createdUser.accountStatus, "status stored");
            assertEquals(email, createdUser.contactInfo, "email stored");
            assertEquals(fullName, createdUser.fullName, "full name stored");
            assertEquals(phoneNumber, createdUser.phoneNumber, "phone stored");
        });

        check("signup rejects unsupported email domain", () -> {
            String username = "signup_bad_" + System.currentTimeMillis();
            AuthResult res = auth.signUp(username, username + "@example.com", "secret123", "Bad Signup", "555-0109");
            assertFalse(res.isSuccess(), "Expected signup failure");
        });

        System.out.println();
        if (failures == 0) {
            System.out.println("All authentication tests PASSED");
        } else {
            System.err.println(failures + " test(s) FAILED");
            System.exit(1);
        }
    }

    private static void seedIfMissing(UserDao dao, String u, String p, String r, String c,
                                      String fullName, String phoneNumber) throws Exception {
        if (dao.findByUsername(u) == null) dao.createUser(u, p, r, c, fullName, phoneNumber);
    }

    // Tiny assertion helpers
    private static void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    private static void assertFalse(boolean cond, String msg) {
        if (cond) throw new AssertionError(msg);
    }

    private static void assertEquals(Object exp, Object act, String label) {
        if (exp == null ? act != null : !exp.equals(act)) {
            throw new AssertionError(label + ": expected=" + exp + ", actual=" + act);
        }
    }

    private static void check(String name, CheckedRunnable r) {
        try {
            r.run();
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            failures++;
            System.err.println("[FAIL] " + name + " -> " + t.getMessage());
        }
    }

    @FunctionalInterface
    interface CheckedRunnable { void run() throws Exception; }
}
