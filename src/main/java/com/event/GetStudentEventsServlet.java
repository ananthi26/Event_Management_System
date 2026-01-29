package com.event;

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

@WebServlet("/GetStudentEventsServlet")
public class GetStudentEventsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentEmail") == null) {
            response.getWriter().print("[]");
            return;
        }

        String email = session.getAttribute("studentEmail").toString();
        List<Map<String, String>> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT e.* FROM events e " +
                "JOIN event_registrations r ON e.id=r.event_id " +
                "WHERE r.student_email=?"
            );
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String,String> e = new HashMap<>();
                e.put("name", rs.getString("name"));
                e.put("start", rs.getString("start_date"));
                e.put("end", rs.getString("end_date"));
                e.put("venue", rs.getString("venue"));
                list.add(e);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        response.setContentType("application/json");
        response.getWriter().print(new Gson().toJson(list));
    }
}
