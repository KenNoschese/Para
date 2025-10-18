package org.example.DatabaseManager.UserDatabase;

import org.example.DatabaseManager.DatabaseInstance;
import javax.swing.*;
import java.sql.*;

public class UserManager {
    private final Connection con;

    public UserManager() {
        this.con = DatabaseInstance.getInstance().getConnection();
    }

    public void signUpUser(String name, String category) {
        try (Statement st = con.createStatement()) {
            String table;
            String idColumn;
            String nameColumn;

            // Determine correct table and column names based on category
            switch (category.toLowerCase()) {
                case "student" -> {
                    table = "Students";
                    idColumn = "stu_id";
                    nameColumn = "stu_name";
                }
                case "pwd" -> {
                    table = "PWDs";
                    idColumn = "pwd_id";
                    nameColumn = "pwd_name";
                }
                case "senior citizen" -> {
                    table = "SeniorCitizens";
                    idColumn = "sen_id";
                    nameColumn = "sen_name";
                }
                default -> { // Regulars
                    table = "Regulars";
                    idColumn = "reg_id";
                    nameColumn = "reg_name";
                }
            }

            String insertQuery = String.format("INSERT INTO %s (%s) VALUES ('%s')", table, nameColumn, name);
            st.executeUpdate(insertQuery);

            // Retrieve the new auto-incremented ID
            int newId = 0;
            ResultSet rs = st.executeQuery("SELECT MAX(" + idColumn + ") AS maxid FROM " + table);
            if (rs.next()) {
                newId = rs.getInt("maxid");
            }
            rs.close();
            System.out.println("🆔 Retrieved new ID: " + newId);

            // Create DB user credentials
            String firstName = name.split(" ")[0];
            String username = firstName;
            String password = newId + firstName;

            st.executeUpdate(String.format("CREATE USER '%s'@'%%' IDENTIFIED BY '%s'", username, password));
            System.out.println("Database user created: " + username);

            //Grant SELECT privilege only on route_schema
            String grantQuery = String.format("GRANT SELECT ON route_schema.* TO '%s'@'%%'", username);
            st.executeUpdate(grantQuery);
            st.executeUpdate("FLUSH PRIVILEGES");
            System.out.println("Granted SELECT privileges to " + username + " on route_schema");

            //Confirmation dialog // Basig naa pa kay better alternative ani ken, gi JOption ra nako kay wakoy idea unsaon
            JOptionPane.showMessageDialog(
                    null,
                    "Account Created Successfully!\n\nUsername: " + username + "\nPassword: " + password,
                    "Signup Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Signup failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Unexpected error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}