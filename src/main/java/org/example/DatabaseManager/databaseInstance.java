package org.example.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseInstance {
    private static databaseInstance instance;
    private Connection connection;
    private Statement statement;

    private final String db = "route_schema";
    private String uname = "root";
    private String pswd = "1234";

    private databaseInstance(String uname, String pswd) {
        try {
            this.uname = uname;
            this.pswd = pswd;

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/" + db + "?serverTimezone=UTC",
                    uname, pswd
            );
            statement = connection.createStatement();

            System.out.println("Connected to database as: " + uname);
        } catch (Exception e) {
            System.err.println("Failed to connect as " + uname + ": " + e.getMessage());
        }
    }

    private databaseInstance() {
        this("root", "1234");
    }

    public static synchronized databaseInstance getInstance() {
        if (instance == null) {
            instance = new databaseInstance(); // default root
        }
        return instance;
    }

    public static synchronized boolean loginAsUser(String username, String password) {
        try {
            if (instance != null) {
                instance.close();
            }

            instance = new databaseInstance(username, password);

            if (instance.connection != null && instance.connection.isValid(2)) {
                System.out.println("Logged in successfully as " + username);
                return true;
            } else {
                System.out.println("⚠Login failed for " + username);
                instance = null;
                return false;
            }

        } catch (SQLException e) {
            System.out.println("SQL Error during login: " + e.getMessage());
            instance = null;
            return false;
        }
    }

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
            System.out.println("Database connection closed for user: " + uname);
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
