package org.example.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInstance {
    private static DatabaseInstance instance;
    private Connection connection;
    private Statement statement;

    private final String db = "route_schema";
    private String uname = "root";
    private String pswd = "Ken11514!";

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

            System.out.println("Connected to database as: " + uname);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private DatabaseInstance() {
        this("root", "Ken11514!");
    }

    public static synchronized DatabaseInstance getInstance() {
        if (instance == null) {
            instance = new DatabaseInstance();
        }
        return instance;
    }

    public static synchronized boolean loginAsUser(String username, String password) {
        try {
            if (instance != null) {
                instance.close();
            }

            instance = new DatabaseInstance(username, password);

            if (instance.connection != null && instance.connection.isValid(2)) {
                return true;
            } else {
                instance = null;
                return false;
            }

        } catch (SQLException e) {
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
