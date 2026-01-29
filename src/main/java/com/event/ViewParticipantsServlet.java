package com.event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet("/ViewParticipantsServlet")
public class ViewParticipantsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT e.name, r.student_email FROM registrations r JOIN events e ON r.event_id=e.id";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            out.println("<table border='1'>");
            out.println("<tr><th>Event</th><th>Student</th></tr>");
            while (rs.next()) {
                out.println("<tr><td>" + rs.getString(1) + "</td><td>"
                        + rs.getString(2) + "</td></tr>");
            }
            out.println("</table>");

        } catch (Exception e) {
            out.println("Error loading participants");
        }
    }
}
