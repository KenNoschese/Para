// DatabaseInstance

package org.example.DatabaseManager;

import java.sql.*;

public class DatabaseInstance {

    private static volatile DatabaseInstance instance;
    private Connection connection;
    private Statement statement;

    private final String db = "route_schema";

    private String uname = "root";
    private String pswd = "Ken11514!";

    private static String currentAppUser;

    private static final String JDBC_URL =
            "jdbc:mysql://127.0.0.1:3306/route_schema?serverTimezone=UTC&allowPublicKeyRetrieval=true";

    public static void setLoggedInUser(String username) {
        currentAppUser = username;
    }

    public static String getCurrentAppUser() {
        return currentAppUser;
    }

    private synchronized void connectAsUser(String username, String password) throws SQLException {
        closeCurrentConnection();

        try {
            connection = DriverManager.getConnection(JDBC_URL, username, password);
            statement = connection.createStatement();
            this.uname = username;
            this.pswd = password;

            System.out.println("Connected to database as user: " + username);
        } catch (SQLException e) {
            System.err.println("Failed to connect as '" + username + "': " + e.getMessage());
            throw e;
        }
    }

    private synchronized void ensureConnection() throws SQLException {
        if (connection != null && !connection.isClosed() && connection.isValid(1)) {
            return;
        }

        closeCurrentConnection();
        connection = DriverManager.getConnection(JDBC_URL, uname, pswd);
        statement = connection.createStatement();
        System.out.println("Re-connected to database as: " + uname);
    }

    private synchronized void closeCurrentConnection() {
        try { if (statement != null) { statement.close(); statement = null; } } catch (SQLException ignored) {}
        try { if (connection != null && !connection.isClosed()) { connection.close(); connection = null; } } catch (SQLException ignored) {}
    }

    private DatabaseInstance() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ensureConnection();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise DB singleton", e);
        }
    }

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

    public Connection getConnection() {
        try {
            ensureConnection();
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Could not obtain valid DB connection", e);
        }
    }

    public Statement getStatement() {
        try {
            ensureConnection();
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException("Could not obtain valid DB statement", e);
        }
    }

    public synchronized void forceRootConnection() throws SQLException {
        closeCurrentConnection();
        connectAsUser("root", "Ken11514!");
        System.out.println("Restored root connection for login");
    }

    public synchronized void forceSwitchToUser(String username, String password) {
        try {
            connectAsUser(username, password);
            setLoggedInUser(username);
        } catch (SQLException e) {
            System.err.println("Failed to switch to user: " + e.getMessage());
            throw new RuntimeException("Failed to switch DB user", e);
        }
    }

    public static boolean loginAsUser(String username, String password) {
        DatabaseInstance db = getInstance();

        try {
            db.forceRootConnection();
        } catch (SQLException e) {
            System.err.println("Failed to restore root: " + e.getMessage());
            return false;
        }

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT password FROM UserAccounts WHERE username = ?")) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("password").equals(password)) {
                    db.forceSwitchToUser(username, password);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return false;
    }

    public synchronized void close() {
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