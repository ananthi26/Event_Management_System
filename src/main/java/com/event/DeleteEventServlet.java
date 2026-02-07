package com.event;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@WebServlet("/DeleteEventServlet")
public class DeleteEventServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        String id = req.getParameter("eventId");

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps1 =
              con.prepareStatement("DELETE FROM event_registrations WHERE event_id=?");
            ps1.setInt(1, Integer.parseInt(id));
            ps1.executeUpdate();

            PreparedStatement ps2 =
              con.prepareStatement("DELETE FROM events WHERE id=?");
            ps2.setInt(1, Integer.parseInt(id));
            ps2.executeUpdate();

            res.getWriter().print("Event deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().print("Error deleting event");
        }
    }
}
