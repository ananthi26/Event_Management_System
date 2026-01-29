package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/FacultyLoginServlet")
@MultipartConfig
public class FacultyLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/plain");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT id FROM faculty WHERE email=? AND password=?"
            );
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("facultyEmail", email);
                response.getWriter().print("success");
            } else {
                response.getWriter().print("fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("fail");
        }
    }
}
