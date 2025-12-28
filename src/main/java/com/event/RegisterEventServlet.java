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

import java.io.PrintWriter;



@WebServlet("/RegisterEventServlet")
public class RegisterEventServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentUsername = request.getParameter("studentUsername");
        int eventId = Integer.parseInt(request.getParameter("eventId"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBConnection.getConnection()) {
            String checkQuery = "SELECT * FROM registrations WHERE event_id = ? AND student_username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, eventId);
            checkStmt.setString(2, studentUsername);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                out.write("❌ Already registered for this event.");
                return;
            }

            String insertQuery = "INSERT INTO registrations (event_id, student_username) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, eventId);
            insertStmt.setString(2, studentUsername);

            int rows = insertStmt.executeUpdate();
            if (rows > 0) {
                out.write("✅ Registration successful!");
            } else {
                out.write("❌ Registration failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.write("❌ DB Error: " + e.getMessage());
        }
    }
}
