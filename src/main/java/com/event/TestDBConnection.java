package com.event;

import java.sql.Connection;

public class TestDBConnection {

    public static void main(String[] args) {

        Connection c = DBConnection.getConnection();

        if (c != null) {
            System.out.println("Database connected successfully!");
        } else {
            System.out.println("Database connection failed");
        }
    }
}
