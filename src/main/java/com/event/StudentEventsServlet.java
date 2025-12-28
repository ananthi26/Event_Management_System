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

@WebServlet("/StudentEventsServlet")
public class StudentEventsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBConnection.getConnection()) {

            String query = "SELECT * FROM events WHERE status = 'upcoming' ORDER BY start_time ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            out.println("<h2>Available Events</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>Name</th><th>Start</th><th>End</th><th>Venue</th><th>Type</th><th>Action</th></tr>");

            while (rs.next()) {
                int eventId = rs.getInt("id");

                out.println("<tr>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("start_time") + "</td>");
                out.println("<td>" + rs.getString("end_time") + "</td>");
                out.println("<td>" + rs.getString("venue") + "</td>");
                out.println("<td>" + rs.getString("type") + "</td>");
                out.println("<td>");

                out.println("<form method='post' action='RegisterEventServlet'>");
                out.println("<input type='hidden' name='eventId' value='" + eventId + "'/>");
                out.println("<input type='text' name='studentUsername' placeholder='Enter your username' required/>");
                out.println("<input type='submit' value='Register'/>");
                out.println("</form>");

                out.println("</td>");
                out.println("</tr>");
            }

            out.println("</table>");

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("❌ Error loading events: " + e.getMessage());
        }
    }
}
