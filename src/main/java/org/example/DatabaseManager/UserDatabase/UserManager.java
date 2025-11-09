package org.example.DatabaseManager.UserDatabase;

import org.example.DatabaseManager.DatabaseInstance;

import javax.swing.*;
import java.sql.*;

public class UserManager {

    public boolean signUpUser(String username, String password) {
        Connection rootConn = null;
        try {
            // 1. Get ROOT connection
            DatabaseInstance db = DatabaseInstance.getInstance();
            db.forceRootConnection();  // ensures we're root
            rootConn = db.getConnection();

            // 2. Create MySQL user
            String createUserSQL = "CREATE USER IF NOT EXISTS ?@'127.0.0.1' IDENTIFIED BY ?";
            try (PreparedStatement ps = rootConn.prepareStatement(createUserSQL)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                System.out.println("MySQL user created: " + username);
            }

            // 3. Grant privileges
            String grantSQL = "GRANT ALL PRIVILEGES ON para_schema.* TO ?@'127.0.0.1'";
            try (PreparedStatement ps = rootConn.prepareStatement(grantSQL)) {
                ps.setString(1, username);
                ps.executeUpdate();
                System.out.println("Granted privileges to: " + username);
            }

            // 4. Flush privileges
            try (Statement stmt = rootConn.createStatement()) {
                stmt.executeUpdate("FLUSH PRIVILEGES");
            }

            // 5. Insert into UserAccounts (app-level tracking)
            String insertSQL = "INSERT IGNORE INTO UserAccounts (username, password) VALUES (?, ?)";
            try (PreparedStatement ps = rootConn.prepareStatement(insertSQL)) {
                ps.setString(1, username);
                ps.setString(2, password);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("App user recorded: " + username);
                } else {
                    System.out.println("Username already exists in app DB");
                    return false;
                }
            }

            // 6. Test login immediately
            return DatabaseInstance.loginAsUser(username, password);

        } catch (SQLException e) {
            System.err.println("Signup failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Signup failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}