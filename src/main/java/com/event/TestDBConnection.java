package com.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println("Connection established! Running test query...");

            try {
                String sql = "SELECT * FROM users";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String username = rs.getString("username");
                    String role = rs.getString("role");
                    System.out.println("User: " + username + ", Role: " + role);
                }

                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println("SQL error: " + e.getMessage());
            }

        } else {
            System.out.println("Failed to establish connection.");
        }
    }
}
