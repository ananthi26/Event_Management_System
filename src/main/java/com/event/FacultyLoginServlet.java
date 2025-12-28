package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;

@WebServlet("/FacultyLoginServlet")
public class FacultyLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        
        if (email == null || password == null ||
            !email.endsWith("@srec.ac.in")) {

            response.sendRedirect("faculty-login.html?error=invalid");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT * FROM faculty WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                
                HttpSession session = request.getSession();
                session.setAttribute("role", "faculty");
                session.setAttribute("email", email);

                response.sendRedirect("FacultyDashboardServlet");
            } else {
                
                response.sendRedirect("faculty-login.html?error=invalid");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("faculty-login.html?error=db");
        }
    }
}
