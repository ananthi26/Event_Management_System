package com.event;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ManageEventsServlet")
public class ManageEventsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBConnection.getConnection()) {

            String query = "SELECT * FROM events ORDER BY start_time DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            out.println("<!DOCTYPE html>");
            out.println("<html><head>");
            out.println("<meta charset='UTF-8'><title>Manage My Events</title>");
            out.println("<link rel='stylesheet' href='style/styles.css'>");
            out.println("</head><body>");

            out.println("<nav class='top-nav'>");
            out.println("<div class='nav-title'>🎓 Event Scheduler - Faculty</div>");
            out.println("<div class='nav-links'>");
            out.println("<a href='faculty-dashboard.html'>Dashboard</a>");
            out.println("<a href='ManageEventsServlet'>Manage Events</a>");
            out.println("<a href='index.html'>Logout</a>");
            out.println("</div></nav>");

            out.println("<div class='dashboard-container'>");
            out.println("<h2>Manage My Events</h2>");
            out.println("<div class='event-card-list'>");

            while (rs.next()) {
                String name = rs.getString("name");
                String startStr = rs.getString("start_time");
                String endStr = rs.getString("end_time");
                String venue = rs.getString("venue");
                String type = rs.getString("type");

                LocalDateTime startTime = LocalDateTime.parse(startStr, formatter);
                LocalDateTime endTime = LocalDateTime.parse(endStr, formatter);

                String status;
                if (now.isBefore(startTime)) {
                    status = "Upcoming";
                } else if (now.isAfter(endTime)) {
                    status = "Completed";
                } else {
                    status = "Ongoing";
                }

                out.println("<div class='event-card'>");
                out.println("<h3>" + name + "</h3>");
                out.println("<p><strong>Start:</strong> " + startStr + "</p>");
                out.println("<p><strong>End:</strong> " + endStr + "</p>");
                out.println("<p><strong>Venue:</strong> " + venue + "</p>");
                out.println("<p><strong>Type:</strong> " + type + "</p>");
                out.println("<p><strong>Status:</strong> " + status + "</p>");
                out.println("</div>");
            }

            out.println("</div></div>");
            out.println("</body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Database error: " + e.getMessage());
        }
    }
}
