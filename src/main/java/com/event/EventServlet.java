package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/EventServlet")
@MultipartConfig
public class EventServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/plain");

        System.out.println("========== EventServlet START ==========");

        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("SESSION NULL");
            response.getWriter().print("unauthorized");
            return;
        }

        Object faculty = session.getAttribute("facultyEmail");
        System.out.println("SESSION facultyEmail = " + faculty);

        if (faculty == null) {
            response.getWriter().print("unauthorized");
            return;
        }

        String name = request.getParameter("eventName");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String venue = request.getParameter("venue");
        String type = request.getParameter("eventType");
        String organizer = request.getParameter("organizer");
        String maxStr = request.getParameter("maxParticipants");

        if (name == null || startDate == null || endDate == null ||
            venue == null || type == null || organizer == null || maxStr == null) {

            System.out.println("❌ ONE OR MORE PARAMETERS ARE NULL");
            response.getWriter().print("error");
            return;
        }

        try {
            int max = Integer.parseInt(maxStr);

            Connection con = DBConnection.getConnection();
            System.out.println("DB CONNECTION = " + con);

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO events (name, start_date, end_date, venue, type, organizer, max_participants) VALUES (?,?,?,?,?,?,?)"
            );

            ps.setString(1, name);
            ps.setString(2, startDate);
            ps.setString(3, endDate);
            ps.setString(4, venue);
            ps.setString(5, type);
            ps.setString(6, organizer);
            ps.setInt(7, max);

            int rows = ps.executeUpdate();
            System.out.println("ROWS INSERTED = " + rows);

            response.getWriter().print("success");

        } catch (Exception e) {
            System.out.println("❌ EXCEPTION OCCURRED");
            e.printStackTrace();
            response.getWriter().print("error");
        }

        System.out.println("========== EventServlet END ==========");
    }
}
