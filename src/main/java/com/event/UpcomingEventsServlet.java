package com.event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/UpcomingEventsServlet")
public class UpcomingEventsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT * FROM events WHERE start_time > NOW() ORDER BY start_time";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            out.println("<table border='1' width='100%'>");
            out.println("<tr>");
            out.println("<th>Name</th>");
            out.println("<th>Date</th>");
            out.println("<th>Venue</th>");
            out.println("<th>Type</th>");
            out.println("</tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("start_time") + "</td>");
                out.println("<td>" + rs.getString("venue") + "</td>");
                out.println("<td>" + rs.getString("type") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p style='color:red;'>❌ Error loading events</p>");
        }
    }
}
