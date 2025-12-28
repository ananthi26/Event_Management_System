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

@WebServlet("/ViewParticipantsServlet")
public class ViewParticipantsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>View Participants</title>");
        out.println("<link rel='stylesheet' href='style/styles.css'>");
        out.println("</head><body>");

        out.println("<nav class='top-nav'>");
        out.println("<div class='nav-title'>🎓 Event Scheduler - Faculty</div>");
        out.println("<div class='nav-links'>");
        out.println("<a href='faculty-dashboard.html'>Dashboard</a>");
        out.println("<a href='ViewParticipantsServlet'>View Participants</a>");
        out.println("<a href='index.html'>Logout</a>");
        out.println("</div></nav>");

        out.println("<div class='dashboard-container'>");
        out.println("<h2>Approved Participants</h2>");
        out.println("<table border='1' style='width:100%; text-align:left; border-collapse:collapse;'>");
        out.println("<tr><th>Event Name</th><th>Student Name</th></tr>");

        try (Connection conn = DBConnection.getConnection()) {

            String query =
                "SELECT e.name AS event, r.student_name " +
                "FROM event_registrations r " +
                "JOIN events e ON r.event_id = e.id " +
                "WHERE r.status = 'approved'";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("event") + "</td>");
                out.println("<td>" + rs.getString("student_name") + "</td>");
                out.println("</tr>");
            }

        } catch (SQLException e) {
            out.println("<tr><td colspan='2'>Error: " + e.getMessage() + "</td></tr>");
        }

        out.println("</table>");
        out.println("</div></body></html>");
    }
}
