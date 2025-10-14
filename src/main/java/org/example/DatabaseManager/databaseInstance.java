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
    private final String uname = "root";
    private final String pswd = "1234";

    private databaseInstance() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/" + db + "?serverTimezone=UTC",
                    uname, pswd
            );
            statement = connection.createStatement();
            System.out.println("Connected to database (singleton): " + db);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    public static synchronized databaseInstance getInstance() {
        if (instance == null) {
            instance = new databaseInstance();
        }
        return instance;
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
            System.out.println("Singleton database connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
