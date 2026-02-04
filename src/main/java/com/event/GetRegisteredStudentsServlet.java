package com.event;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;

@WebServlet("/GetRegisteredStudentsServlet")
public class GetRegisteredStudentsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");

        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            String sql =
                "SELECT e.name AS event_name, r.student_email " +
                "FROM event_registrations r " +
                "JOIN events e ON r.event_id = e.id " +
                "ORDER BY e.name";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Map<String, List<String>> map = new LinkedHashMap<>();

            while (rs.next()) {
                String eventName = rs.getString("event_name");
                String email = rs.getString("student_email");

                map.computeIfAbsent(eventName, k -> new ArrayList<>())
                   .add(email);
            }

            for (String event : map.keySet()) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("event", event);
                obj.put("students", map.get(event));
                result.add(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().print(new Gson().toJson(result));
    }
}
