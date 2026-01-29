package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;


@WebServlet("/RegisterEventServlet")
@MultipartConfig
public class RegisterEventServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("studentEmail") == null) {
            response.getWriter().print("unauthorized");
            return;
        }

        String studentEmail = session.getAttribute("studentEmail").toString();
        int eventId = Integer.parseInt(request.getParameter("eventId"));

        try (Connection con = DBConnection.getConnection()) {

            // 1️⃣ Prevent duplicate registration
            PreparedStatement check = con.prepareStatement(
                "SELECT * FROM event_registrations WHERE student_email=? AND event_id=?"
            );
            check.setString(1, studentEmail);
            check.setInt(2, eventId);

            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                response.getWriter().print("already");
                return;
            }

            // 2️⃣ Check seats
            PreparedStatement seat = con.prepareStatement(
                "SELECT max_participants FROM events WHERE id=?"
            );
            seat.setInt(1, eventId);
            ResultSet seatRs = seat.executeQuery();

            if (!seatRs.next() || seatRs.getInt(1) <= 0) {
                response.getWriter().print("full");
                return;
            }

            // 3️⃣ Register student
            PreparedStatement insert = con.prepareStatement(
                "INSERT INTO event_registrations(student_email,event_id) VALUES (?,?)"
            );
            insert.setString(1, studentEmail);
            insert.setInt(2, eventId);
            insert.executeUpdate();

            // 4️⃣ Reduce seat count
            PreparedStatement update = con.prepareStatement(
                "UPDATE events SET max_participants = max_participants - 1 WHERE id=?"
            );
            update.setInt(1, eventId);
            update.executeUpdate();

            response.getWriter().print("success");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("error");
        }
    }
}
