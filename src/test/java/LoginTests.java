import com.GoldenOpportunity.AuthenticationController;
import com.GoldenOpportunity.DatabaseTools.DBInitializer;
import com.GoldenOpportunity.DatabaseTools.DBUtil;
import com.GoldenOpportunity.Login.AuthResult;
import com.GoldenOpportunity.Login.LoginResult;
import com.GoldenOpportunity.Login.enums.Role;
import com.GoldenOpportunity.dbLogin.DbUser;
import com.GoldenOpportunity.dbLogin.UserDao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {
    private static Path testDbDir;
    private static UserDao userDao;
    private static AuthenticationController authController;

    @BeforeAll
    static void setup() throws SQLException, IOException {
        testDbDir = Files.createTempDirectory("golden-auth-tests");
        DBUtil.setTestDbDirectory(testDbDir.toString());

        DBInitializer.initialize();

        userDao = new UserDao();
        authController = new AuthenticationController();

        seedIfMissing("admin_test", "adminpass", "ADMIN", "admin_test@golden.com",
                "Admin Test", "555-1101", false);
        seedIfMissing("clerk_test", "clerkpass", "CLERK", "clerk_test@golden.com",
                "Clerk Test", "555-1102", false);
        seedIfMissing("guest_test", "guestpass", "GUEST", "guest_test@gmail.com",
                "Guest Test", "555-1103", true);
    }

    @BeforeEach
    void resetGuestState() throws SQLException {
        userDao.resetAccountState("guest_test");
    }

    @Test
    void loginWithUsernameReturnsSessionAndRole() {
        LoginResult result = authController.logIn("guest_test", "guestpass");

        assertTrue(result.isSuccess());
        assertNotNull(result.getSession());
        assertEquals(Role.GUEST, result.getSession().getRole());
        assertEquals("Authentication successful.", result.getMessage());
    }

    @Test
    void loginWithSupportedEmailWorks() {
        LoginResult result = authController.logIn("guest_test@gmail.com", "guestpass");

        assertTrue(result.isSuccess());
        assertNotNull(result.getSession());
        assertEquals(Role.GUEST, result.getSession().getRole());
    }

    @Test
    void wrongPasswordIncrementsFailureCount() throws SQLException {
        LoginResult result = authController.logIn("guest_test", "wrongpass");

        assertFalse(result.isSuccess());
        assertEquals(1, userDao.getFailedCount("guest_test"));
        assertEquals("ACTIVE", userDao.getStatus("guest_test"));
    }

    @Test
    void thirdFailedAttemptLocksAccount() throws SQLException {
        authController.logIn("guest_test", "bad1");
        authController.logIn("guest_test", "bad2");
        LoginResult thirdResult = authController.logIn("guest_test", "bad3");
        LoginResult lockedResult = authController.logIn("guest_test", "guestpass");

        assertFalse(thirdResult.isSuccess());
        assertEquals("LOCKED", userDao.getStatus("guest_test"));
        assertFalse(lockedResult.isSuccess());
        assertEquals("Account is locked or disabled.", lockedResult.getMessage());
    }

    @Test
    void signUpPersistsGuestUser() throws SQLException {
        String username = "signup_" + System.nanoTime();
        String email = username + "@gmail.com";

        AuthResult result = authController.signUp(
                username, email, "secret123", "Signup User", "555-1199"
        );

        assertTrue(result.isSuccess());

        DbUser created = userDao.findByUsername(username);
        assertNotNull(created);
        assertEquals("GUEST", created.role);
        assertEquals("ACTIVE", created.accountStatus);
        assertEquals(email, created.contactInfo);
        assertEquals("Signup User", created.fullName);
        assertEquals("555-1199", created.phoneNumber);
        assertFalse(created.corporate);
    }

    @Test
    void corporateSignUpPersistsCorporateFlag() throws SQLException {
        String username = "corp_" + System.nanoTime();
        String email = username + "@gmail.com";

        AuthResult result = authController.signUp(
                username, email, "secret123", "Corporate User", "555-1200", true
        );

        assertTrue(result.isSuccess());

        DbUser created = userDao.findByUsername(username);
        assertNotNull(created);
        assertTrue(created.corporate);
        assertTrue(userDao.isCorporate(created.id));
    }

    @Test
    void unsupportedEmailDomainIsRejectedOnSignUp() {
        String username = "bad_domain_" + System.nanoTime();

        AuthResult result = authController.signUp(
                username, username + "@example.com", "secret123", "Bad Domain", "555-1210"
        );

        assertFalse(result.isSuccess());
    }

    @AfterAll
    static void tearDown() throws SQLException, IOException {
        DBUtil.setTestDbDirectory(testDbDir.toString());
        assertTrue(DBUtil.deleteDatabase(testDbDir.resolve("golden.db").toString()));
        Files.deleteIfExists(testDbDir);
        DBUtil.setTestDbDirectory(null);
    }

    private static void seedIfMissing(String username, String password, String role, String email,
                                      String fullName, String phoneNumber, boolean corporate) throws SQLException {
        if (userDao.findByUsername(username) == null) {
            userDao.createUser(username, password, role, email, fullName, phoneNumber, corporate);
        }
    }
}
