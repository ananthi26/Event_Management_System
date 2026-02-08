package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/FacultyRegistrationsServlet")
public class FacultyRegistrationsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("facultyEmail") == null) {
            res.setStatus(401);
            res.getWriter().print("{\"error\":\"session_expired\"}");
            return;
        }

        String facultyEmail = (String) session.getAttribute("facultyEmail");

        try (Connection con = DBConnection.getConnection()) {

            String sql =
                "SELECT e.id AS event_id, e.name AS event_name, r.student_email " +
                "FROM events e " +
                "LEFT JOIN event_registrations r ON e.id = r.event_id " +
                "WHERE e.faculty_email = ? " +
                "ORDER BY e.id DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, facultyEmail);
            ResultSet rs = ps.executeQuery();

          
            java.util.Map<Integer, JsonObject> map = new java.util.HashMap<>();

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("event_name");
                String studentEmail = rs.getString("student_email");

                JsonObject eventObj = map.get(eventId);
                if (eventObj == null) {
                    eventObj = new JsonObject();
                    eventObj.addProperty("eventId", eventId);
                    eventObj.addProperty("eventName", eventName);
                    eventObj.add("students", new JsonArray());
                    map.put(eventId, eventObj);
                }

                if (studentEmail != null) {
                    eventObj.getAsJsonArray("students").add(studentEmail);
                }
            }

            
            JsonArray output = new JsonArray();
            for (JsonObject obj : map.values()) {
                output.add(obj);
            }

            res.getWriter().print(output.toString());

        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().print("{\"error\":\"server_error\"}");
        }
    }
}
