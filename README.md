Golden Opportunity — Project Notes

SQLite user login database setup
- Build: `mvn -q package`
- Initialize DB and seed users: run the class `com.GoldenOpportunity.dbLogin.DbSeeder`.
  - Example (from project root after packaging):
    - `java -cp target/Golden_Opertunity-1.0-SNAPSHOT-jar-with-dependencies.jar com.GoldenOpportunity.dbLogin.DbSeeder`
  - This creates `data/golden.db` and a `users` table with sample accounts:
    - admin1 / adminpass (ADMIN)
    - clerk1 / clerkpass (CLERK)
    - guest1 / guestpass (GUEST)
    - guest2 / guestpass2 (GUEST)

Code overview
- `com.GoldenOpportunity.dbLogin.Database`: manages SQLite connection setup.
- `com.GoldenOpportunity.DatabaseTools.DBIntializer1`: creates the SQLite schema.
- `com.GoldenOpportunity.dbLogin.UserDao`: CRUD and login counters for `users`.
- `com.GoldenOpportunity.dbLogin.PasswordHasher`: PBKDF2 hashing/verification.
- `com.GoldenOpportunity.dbLogin.DbAuthenticationService`: login against SQLite (returns existing `LoginResult`/`Session`).
- `com.GoldenOpportunity.dbLogin.DbSeeder`: one-off initializer and seeder.

Using the DB-backed login in code
- Instantiate `new com.GoldenOpportunity.dbLogin.DbAuthenticationService()` and call `logIn(username, password)`.
- Returns the same `LoginResult` used elsewhere in the project.

Notes
- The database file is stored at `data/golden.db` relative to the project root.
- Passwords are stored as PBKDF2 hashes; do not compare plaintext.

Role capabilities
- `GUEST`: browse hotel pages, search/book rooms, use checkout/shop flows, and access normal login/profile screens.
- `CLERK`: all guest-facing capabilities plus operational tools such as room management and reservation management.
- `ADMIN`: administrative user-directory access, privileged account creation, role filtering, and credential reset actions.

Centralized role checks
- `src/main/java/com/GoldenOpportunity/RolePermissions.java` documents the current role-to-action mapping in one place.
- `UIState` now carries the active authenticated session so screens can check the current role before allowing sensitive actions.

Auth tester
- Run a quick verification against the DB + AuthenticationController:
  - `java -cp target/Golden_Opertunity-1.0-SNAPSHOT-jar-with-dependencies.jar com.GoldenOpportunity.tools.AuthTester`
  - Prints PASS/FAIL for: success login, failed attempts increment, lock after 3, locked login rejection, reset behavior, unknown user.

Runtime warnings
- SLF4J: We include `slf4j-nop` to silence missing-binder messages that come from a transitive `slf4j-api` (1.7.36). If you prefer console logs, swap to `org.slf4j:slf4j-simple:1.7.36` in `pom.xml`.
- JDK 21/22 native access: SQLite loads a native library and newer JDKs warn: `Use --enable-native-access=ALL-UNNAMED`. To suppress, add this JVM arg in your run config, e.g.:
  - IntelliJ Run/Debug Config: `VM options` = `--enable-native-access=ALL-UNNAMED`
  - Plain CLI: `java --enable-native-access=ALL-UNNAMED -cp ... com.GoldenOpportunity.tools.AuthTester`

Guest SQLite tables
- Initialize and sync guest data: run `com.GoldenOpportunity.dbLogin.GuestReservationSeeder`
  - Example:
    - `java -cp target/Golden_Opertunity-1.0-SNAPSHOT-jar-with-dependencies.jar com.GoldenOpportunity.dbLogin.GuestReservationSeeder`
- This creates:
  - `guests`: one row per `users.role = 'GUEST'`, using the existing user `id` as `guest_id`
  - `guest_reservation_summary`: a view for querying guests and their assigned `resId` values
- Current behavior:
  - `GuestReservationSeeder` ensures the SQLite schema exists and syncs guest rows from `users`.
