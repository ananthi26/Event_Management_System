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
import java.io.PrintWriter;




@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

      
        if (email == null || !email.endsWith("@srec.ac.in")) {
            out.println("<script>alert('Only @srec.ac.in emails are allowed!'); window.location='signup.html';</script>");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                out.println("<script>alert('Database connection failed'); window.location='signup.html';</script>");
                return;
            }

            
            String checkSql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                out.println("<script>alert('Email already registered'); window.location='signup.html';</script>");
                return;
            }

           
            String insertSql = "INSERT INTO users (email, password, logged_in) VALUES (?, ?, false)";
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                out.println("<script>alert('Registration successful!'); window.location='index.html';</script>");
            } else {
                out.println("<script>alert('Registration failed.'); window.location='signup.html';</script>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<script>alert('Error: " + e.getMessage() + "'); window.location='signup.html';</script>");
        }
    }
}
