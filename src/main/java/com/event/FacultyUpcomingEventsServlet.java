package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/FacultyUpcomingEventsServlet")
public class FacultyUpcomingEventsServlet extends HttpServlet {

    protected void doGet(javax.servlet.http.HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json");
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            
            PreparedStatement ps = con.prepareStatement(
                "SELECT e.id, e.name, e.start_date, e.end_date, e.venue, e.max_participants, " +
                "  (SELECT COUNT(*) FROM event_registrations r WHERE r.event_id = e.id) AS registered_count " +
                "FROM events e " +
                "WHERE e.end_date >= CURDATE()"
            );

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> e = new HashMap<>();
                e.put("id", rs.getInt("id"));
                e.put("name", rs.getString("name"));
                e.put("start", rs.getString("start_date"));
                e.put("end", rs.getString("end_date"));
                e.put("venue", rs.getString("venue"));

                
                int seatsLeft = rs.getInt("max_participants") - rs.getInt("registered_count");
                e.put("seats", seatsLeft < 0 ? 0 : seatsLeft);

                list.add(e);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        res.getWriter().print(new Gson().toJson(list));
    }
}
