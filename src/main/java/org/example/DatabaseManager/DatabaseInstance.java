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
    private final String uname = "root";
    private final String pswd = "Ken11514!";

    private DatabaseInstance() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/" + db + "?serverTimezone=UTC",
                    uname, pswd
            );
            statement = connection.createStatement();
            System.out.println("✅ Connected to database (singleton): " + db);
        } catch (Exception e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseInstance getInstance() {
        if (instance == null) {
            instance = new DatabaseInstance();
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
            System.out.println("🧹 Singleton database connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

