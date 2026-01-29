package com.event;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;

@WebServlet("/GetUpcomingEventsServlet")
@MultipartConfig
public class GetUpcomingEventsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json");
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT * FROM events WHERE end_date >= CURDATE()";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> e = new HashMap<>();
                e.put("id", rs.getInt("id"));
                e.put("name", rs.getString("name"));
                e.put("start", rs.getString("start_date"));
                e.put("end", rs.getString("end_date"));
                e.put("venue", rs.getString("venue"));
                e.put("organizer", rs.getString("organizer"));
                e.put("max", rs.getInt("max_participants"));
                list.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        res.getWriter().print(new com.google.gson.Gson().toJson(list));
    }
}
