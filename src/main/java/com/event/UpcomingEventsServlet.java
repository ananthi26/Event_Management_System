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


@WebServlet("/UpcomingEventsServlet")
public class UpcomingEventsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT * FROM events WHERE start_time > NOW() ORDER BY start_time";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            out.println("<ul>");
            while (rs.next()) {
                out.println("<li><b>" + rs.getString("name") + "</b> | "
                        + rs.getString("venue") + "</li>");
            }
            out.println("</ul>");

        } catch (Exception e) {
            out.println("Error loading events");
        }
    }
}
