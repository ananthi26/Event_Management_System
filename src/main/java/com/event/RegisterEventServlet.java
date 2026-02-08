package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RegisterEventServlet")
@MultipartConfig
public class RegisterEventServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("text/plain");

        HttpSession session = req.getSession(false);

        // FIXED SESSION CHECK
        if (session == null || session.getAttribute("studentEmail") == null) {
            res.getWriter().print("session_expired");
            return;
        }

        String email = (String) session.getAttribute("studentEmail");
        int eventId = Integer.parseInt(req.getParameter("eventId"));

        try (Connection con = DBConnection.getConnection()) {

            
            PreparedStatement dup = con.prepareStatement(
                "SELECT id FROM event_registrations WHERE event_id=? AND student_email=?"
            );
            dup.setInt(1, eventId);
            dup.setString(2, email);

            if (dup.executeQuery().next()) {
                res.getWriter().print("already_registered");
                return;
            }

           
            PreparedStatement seat = con.prepareStatement(
                "SELECT max_participants FROM events WHERE id=?"
            );
            seat.setInt(1, eventId);
            ResultSet rs = seat.executeQuery();

            if (!rs.next() || rs.getInt(1) <= 0) {
                res.getWriter().print("full");
                return;
            }

           
            PreparedStatement insert = con.prepareStatement(
                "INSERT INTO event_registrations(event_id, student_email) VALUES (?,?)"
            );
            insert.setInt(1, eventId);
            insert.setString(2, email);
            insert.executeUpdate();

           
            PreparedStatement update = con.prepareStatement(
                "UPDATE events SET max_participants = max_participants - 1 WHERE id=? AND max_participants > 0"
            );
            update.setInt(1, eventId);
            update.executeUpdate();

            res.getWriter().print("success");

        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().print("error");
        }
    }
}
