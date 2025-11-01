package org.example.DatabaseManager.UserDatabase;

import org.example.DatabaseManager.DatabaseInstance;

import javax.swing.*;
import java.sql.*;

public class UserManager {
    // Connection is now the *single* app connection
    private final Connection con = DatabaseInstance.getInstance().getConnection();

    public void signUpUser(String name, String password) {
        try {
            // 1. Insert into Regulars → get generated reg_id
            String sqlReg = "INSERT INTO Regulars (reg_name) VALUES (?)";
            int newId;
            try (PreparedStatement ps = con.prepareStatement(sqlReg, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.executeUpdate();
                try (ResultSet gen = ps.getGeneratedKeys()) {
                    gen.next();
                    newId = gen.getInt(1);
                }
            }

            // 2. Build username (firstName + id)
            String firstName = name.split("\\s+")[0];
            String username = firstName + "_" + newId;

            // 3. Insert into UserAccounts
            String sqlAcc = "INSERT INTO UserAccounts (username, password, category, linked_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlAcc)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, "Regular");
                ps.setInt(4, newId);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(null,
                    "Account Created!\nUsername: " + username + "\nPassword: " + password,
                    "Signup Success", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("User created: " + username);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Signup failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("SQL error on signup: " + e.getMessage());
        }
    }
}