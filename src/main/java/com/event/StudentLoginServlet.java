package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/StudentLoginServlet")
@MultipartConfig
public class StudentLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest r, HttpServletResponse s)
            throws IOException {

        s.setContentType("text/plain");

        String email = r.getParameter("email");
        String password = r.getParameter("password");

        if (email == null || password == null) {
            s.getWriter().print("fail");
            return;
        }

        try (Connection c = DBConnection.getConnection()) {

            PreparedStatement ps = c.prepareStatement(
                "SELECT id FROM student WHERE email=? AND password=?"
            );
            ps.setString(1, email.trim());
            ps.setString(2, password.trim());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                HttpSession session = r.getSession(true);

                // EXISTING (keep this)
                session.setAttribute("student", email);

                // ✅ ADD THESE (important)
                session.setAttribute("studentEmail", email);
                session.setAttribute("studentName", "ANANTHI G"); // or fetch from DB
                session.setAttribute("studentDept", "CSE");

                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                s.getWriter().print("success");
            }


        } catch (Exception e) {
            e.printStackTrace();
            s.getWriter().print("fail");
        }
    }
}