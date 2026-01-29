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


@WebServlet("/CompletedEventsServlet")
public class CompletedEventsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT * FROM events WHERE end_time < NOW()";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            out.println("<ul>");
            while (rs.next()) {
                out.println("<li>" + rs.getString("name") + "</li>");
            }
            out.println("</ul>");

        } catch (Exception e) {
            out.println("Error loading completed events");
        }
    }
}
