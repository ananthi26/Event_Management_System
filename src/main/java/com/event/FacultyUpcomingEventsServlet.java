package com.event;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.Gson;
@WebServlet("/FacultyUpcomingEventsServlet")
public class FacultyUpcomingEventsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json");
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
              "SELECT id,name,start_date,end_date,venue,max_participants FROM events"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> e = new HashMap<>();
                e.put("id", rs.getInt("id"));
                e.put("name", rs.getString("name"));
                e.put("start", rs.getString("start_date"));
                e.put("end", rs.getString("end_date"));
                e.put("venue", rs.getString("venue"));
                e.put("seats", rs.getInt("max_participants"));
                list.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        res.getWriter().print(new Gson().toJson(list));
    }
}
