package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;

@WebServlet("/StudentUpcomingEventsServlet")
@MultipartConfig
public class StudentUpcomingEventsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("studentEmail") == null) {
            res.sendError(401);
            return;
        }

        String studentEmail = (String) session.getAttribute("studentEmail");
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            
            PreparedStatement ps = con.prepareStatement(
                "SELECT e.*, " +
                " (SELECT COUNT(*) FROM event_registrations r WHERE r.event_id = e.id) AS registered_count, " +
                " (SELECT COUNT(*) FROM event_registrations r WHERE r.event_id = e.id AND r.student_email = ?) AS registered_for_you " +
                "FROM events e " +
                "WHERE e.end_date >= CURDATE()"
            );

            ps.setString(1, studentEmail);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("name", rs.getString("name"));
                map.put("start", rs.getDate("start_date"));
                map.put("end", rs.getDate("end_date"));
                map.put("venue", rs.getString("venue"));

               
                int seatsLeft = rs.getInt("max_participants") - rs.getInt("registered_count");
                map.put("seats", seatsLeft < 0 ? 0 : seatsLeft);

                
                map.put("registered", rs.getInt("registered_for_you") > 0);

                list.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        res.getWriter().print(new Gson().toJson(list));
    }
}
