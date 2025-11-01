package org.example.DatabaseManager;

import java.sql.*;

public class DatabaseInstance {
    private static DatabaseInstance instance;
    private Connection connection;
    private Statement statement;

    private final String db = "route_schema";

    // Default root credentials (used for admin operations like sign-up)
    private String uname = "root";
    private String pswd = "1234";

    // Track currently logged-in app user and category
    private static String currentAppUser;
    private static String currentAppCategory;

    // ===================== USER TRACKING =====================
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

    // ===================== CONNECTION MANAGEMENT =====================

    /**
     * Connect to the database as a specific user (non-root).
     */
    public void connectAsUser(String username, String password) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

            connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/" + db + "?serverTimezone=UTC",
                    username, password
            );
            statement = connection.createStatement();

            this.uname = username;
            this.pswd = password;
            setLoggedInUser(username, null);

            System.out.println("✅ Connected to database as user: " + username);
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect as user: " + e.getMessage());
        }
    }

    /**
     * Default constructor — connects as root (admin).
     */
    private DatabaseInstance(String uname, String pswd) {
        try {
            this.uname = uname;
            this.pswd = pswd;

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/" + db + "?serverTimezone=UTC",
                    uname, pswd
            );
            statement = connection.createStatement();

            System.out.println("✅ Connected to database as: " + uname);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Default singleton initialization (root by default).
     */
    private DatabaseInstance() {
        this("root", "1234");
    }

    public static synchronized DatabaseInstance getInstance() {
        if (instance == null) {
            instance = new DatabaseInstance();
        }
        return instance;
    }

    // ===================== LOGIN HANDLING =====================

    /**
     * Verify credentials from UserAccounts table, then reconnect as that user.
     */
    public static boolean loginAsUser(String username, String password) {
        try (Connection con = getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT username, password FROM UserAccounts WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPass = rs.getString("password");

                if (storedPass.equals(password)) {
                    System.out.println("✅ User verified: " + username);

                    // Switch DB connection from root → user
                    getInstance().connectAsUser(username, password);
                    return true;
                } else {
                    System.out.println("❌ Invalid password for user: " + username);
                }
            } else {
                System.out.println("❌ User not found: " + username);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error during login: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during login: " + e.getMessage());
        }

        return false;
    }

    // ===================== UTILITY =====================

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public void close() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            instance = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getActiveUsername() {
        return uname;
    }

    public String getActivePassword() {
        return pswd;
    }
}