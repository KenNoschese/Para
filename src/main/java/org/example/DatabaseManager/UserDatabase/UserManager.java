package org.example.DatabaseManager.UserDatabase;

import org.example.DatabaseManager.DatabaseInstance;

import javax.swing.*;
import java.sql.*;

public class UserManager {

    public boolean signUpUser(String username, String password) {
        String sql = "INSERT INTO UserAccounts (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User created: " + username);
                return true;
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                System.out.println("Username already exists: " + username);
            } else {
                System.err.println("Signup failed: " + e.getMessage());
            }
        }
        return false;
    }
}