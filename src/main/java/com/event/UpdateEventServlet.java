package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UpdateEventServlet")
@MultipartConfig
public class UpdateEventServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");

        try {
            // Read form parameters
            int id = Integer.parseInt(req.getParameter("id"));
            String name = req.getParameter("name");
            String startDate = req.getParameter("start_date");
            String endDate = req.getParameter("end_date");
            String venue = req.getParameter("venue");
            int maxParticipants = Integer.parseInt(req.getParameter("max_participants"));

            System.out.println("Parsed ID: " + id);

            Connection conn = DBConnection.getConnection();

            if (conn == null) {
                System.out.println("❌ DB CONNECTION FAILED!");
                resp.getWriter().write("error");
                return;
            }

            // SQL Update Query
            String sql = "UPDATE events SET name=?, start_date=?, end_date=?, venue=?, max_participants=? WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, startDate);
            ps.setString(3, endDate);
            ps.setString(4, venue);
            ps.setInt(5, maxParticipants);
            ps.setInt(6, id);

            int rows = ps.executeUpdate();

            System.out.println("Rows updated: " + rows);

            if (rows > 0) {
                resp.getWriter().write("success");
            } else {
                System.out.println("❌ Update failed — ID may not exist in DB");
                resp.getWriter().write("failed");
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("error");
        }
    }
}
