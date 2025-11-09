package org.example.DatabaseManager;

import java.sql.*;

public class DatabaseInstance {

    /* ============================================================== */
    /* ====================== SINGLETON FIELDS ====================== */
    /* ============================================================== */

    private static volatile DatabaseInstance instance;   // volatile for thread-safety
    private Connection connection;
    private Statement statement;

    private final String db = "route_schema";

    // Default root credentials (admin)
    private String uname = "root";
    private String pswd = "Ken11514!";

    // Currently logged-in app user / category
    private static String currentAppUser;
    private static String currentAppCategory;

    private static final String JDBC_URL =
            "jdbc:mysql://127.0.0.1:3306/" + "route_schema" + "?serverTimezone=UTC";

    /* ============================================================== */
    /* ====================== USER TRACKING ======================== */
    /* ============================================================== */

    public static void setLoggedInUser(String username, String category) {
        currentAppUser = username;
        currentAppCategory = category;
    }

    public static String getCurrentAppUser() {
        return currentAppUser;
    }

    public static String getCurrentAppCategory() {
        return currentAppCategory;
    }

    /* ============================================================== */
    /* ====================== CONNECTION MANAGEMENT ================ */
    /* ============================================================== */

    /**
     * Connect (or reconnect) as a **specific** MySQL user.
     */
    private void connectAsUser(String username, String password) throws SQLException {
        closeCurrentConnection();   // clean any previous connection

        connection = DriverManager.getConnection(JDBC_URL, username, password);
        statement = connection.createStatement();

        this.uname = username;
        this.pswd = password;
        setLoggedInUser(username, null);

        System.out.println("Connected to database as user: " + username);
    }

    /**
     * Ensure the current connection is alive.
     * If it is closed or invalid, a fresh connection is opened with the
     * **same credentials** that were used the last time.
     */
    private synchronized void ensureConnection() throws SQLException {
        if (connection != null && connection.isValid(1)) {
            return;   // still good
        }

        // Connection is dead -> reconnect with the *current* credentials
        closeCurrentConnection();
        connection = DriverManager.getConnection(JDBC_URL, uname, pswd);
        statement = connection.createStatement();
        System.out.println("Re-connected to database as: " + uname);
    }

    /** Close the current connection & statement safely. */
    private void closeCurrentConnection() {
        try { if (statement != null) statement.close(); } catch (SQLException ignored) {}
        try { if (connection != null) connection.close(); } catch (SQLException ignored) {}
        statement = null;
        connection = null;
    }

    /* ============================================================== */
    /* ====================== PUBLIC API ============================ */
    /* ============================================================== */

    private DatabaseInstance() {
        // private -> forces use of getInstance()
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ensureConnection();                 // opens root connection
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise DB singleton", e);
        }
    }

    /** Thread-safe lazy singleton. */
    public static DatabaseInstance getInstance() {
        if (instance == null) {
            synchronized (DatabaseInstance.class) {
                if (instance == null) {
                    instance = new DatabaseInstance();
                }
            }
        }
        return instance;
    }

    /**
     * Return a **live** connection.
     * Callers should *always* use try-with-resources when they need a
     * PreparedStatement / ResultSet.
     */
    public Connection getConnection() {
        try {
            ensureConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Could not obtain a valid DB connection", e);
        }
        return connection;
    }

    public Statement getStatement() {
        try {
            ensureConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Could not obtain a valid DB statement", e);
        }
        return statement;
    }

    /** Switch the DB session to a different MySQL user (after login). */
    public void switchToUser(String username, String password) {
        try {
            connectAsUser(username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to switch DB user", e);
        }
    }

    /** Login helper – verifies credentials and switches connection. */
    public static boolean loginAsUser(String username, String password) {
        DatabaseInstance db = getInstance();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT username, password FROM UserAccounts WHERE username = ?")) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("password").equals(password)) {
                    db.switchToUser(username, password);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error during login: " + e.getMessage());
        }
        return false;
    }

    /* ============================================================== */
    /* ====================== CLEANUP =============================== */
    /* ============================================================== */

    public void close() {
        closeCurrentConnection();
        synchronized (DatabaseInstance.class) {
            instance = null;
        }
    }

    public String getActiveUsername() {
        return uname;
    }

    public String getActivePassword() {
        return pswd;
    }
}