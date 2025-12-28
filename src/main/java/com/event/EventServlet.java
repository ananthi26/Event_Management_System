package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EventServlet")
public class EventServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        String name = request.getParameter("eventName");
        String description = request.getParameter("description");
        String startTime = request.getParameter("startDateTime");
        String endTime = request.getParameter("endDateTime");
        String venue = request.getParameter("venue");
        String type = request.getParameter("eventType");
        String organizer = request.getParameter("organizer");
        String maxParticipantsStr = request.getParameter("maxParticipants");

        
        if (name == null || description == null || startTime == null || endTime == null ||
            venue == null || type == null || organizer == null || maxParticipantsStr == null ||
            name.isEmpty() || maxParticipantsStr.isEmpty()) {

            response.sendRedirect("create-event.html?error=Please+fill+all+fields");
            return;
        }

        int maxParticipants;
        try {
            maxParticipants = Integer.parseInt(maxParticipantsStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("create-event.html?error=Invalid+number+for+max+participants");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                response.sendRedirect("create-event.html?error=Database+connection+failed");
                return;
            }

            String sql = "INSERT INTO events (name, description, start_time, end_time, venue, type, organizer, max_participants) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setString(3, startTime);
                stmt.setString(4, endTime);
                stmt.setString(5, venue);
                stmt.setString(6, type);
                stmt.setString(7, organizer);
                stmt.setInt(8, maxParticipants);

                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    response.sendRedirect("create-event.html?success=true");
                } else {
                    response.sendRedirect("create-event.html?error=Failed+to+create+event");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("create-event.html?error=Database+error");
        }
    }
}
