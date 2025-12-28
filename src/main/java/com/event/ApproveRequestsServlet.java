package com.event;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ApproveRequestsServlet")
public class ApproveRequestsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBConnection.getConnection()) {

            String query =
                "SELECT r.id, r.student_name, e.name " +
                "FROM event_registrations r " +
                "JOIN events e ON r.event_id = e.id " +
                "WHERE r.status = 'pending'";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            out.println("<html><head><title>Approve Requests</title></head><body>");
            out.println("<h2>Approve Student Requests</h2>");
            out.println("<form method='post' action='ApproveRequestsServlet'>");

            while (rs.next()) {
                int id = rs.getInt("id");
                String student = rs.getString("student_name");
                String event = rs.getString("name");

                out.println("<div>");
                out.println("<input type='checkbox' name='request' value='" + id + "'/> ");
                out.println("Student: " + student + " | Event: " + event);
                out.println("</div>");
            }

            out.println("<button type='submit'>Approve Selected</button>");
            out.println("</form></body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("❌ Error loading requests.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] selected = request.getParameterValues("request");

        if (selected != null) {
            try (Connection conn = DBConnection.getConnection()) {

                String updateSQL =
                        "UPDATE event_registrations SET status='approved' WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(updateSQL);

                for (String id : selected) {
                    stmt.setInt(1, Integer.parseInt(id));
                    stmt.executeUpdate();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect("ApproveRequestsServlet");
    }
}
