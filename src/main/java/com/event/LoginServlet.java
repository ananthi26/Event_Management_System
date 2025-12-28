package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            response.sendRedirect("index.html?msg=dberror");
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE email=? AND password=?");
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                boolean isLoggedIn = rs.getBoolean("logged_in");
                String role = rs.getString("role");

                if (isLoggedIn) {
                    // User already logged in elsewhere
                    response.sendRedirect("index.html?msg=loggedin");
                } else {
                    // Mark user as logged in
                    PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE users SET logged_in=true WHERE email=?");
                    updateStmt.setString(1, email);
                    updateStmt.executeUpdate();

                    // Redirect to appropriate dashboard
                    if ("faculty".equalsIgnoreCase(role)) {
                        response.sendRedirect("FacultyDashboardServlet");
                    } else if ("student".equalsIgnoreCase(role)) {
                        response.sendRedirect("student-dashboard.html");
                    } else {
                        response.sendRedirect("index.html");
                    }
                }
            } else {
                // Invalid credentials
                response.sendRedirect("index.html?msg=invalid");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("index.html?msg=dberror");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("❌ Error closing DB connection: " + e.getMessage());
            }
        }
    }
}
