package com.event;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/event_management_system";
    private static final String USER = "root";
    private static final String PASSWORD = "Anvii@2602";

    public static Connection getConnection() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL Driver not found: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to database: " + e.getMessage());
            return null;
        }
    }
}
