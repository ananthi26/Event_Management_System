package com.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;


@WebServlet("/FacultyDashboardServlet")
public class FacultyDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Not logged in
        if (session == null ||
            !"faculty".equals(session.getAttribute("role"))) {

            response.sendRedirect("faculty-login.html");
            return;
        }

        // Faculty allowed
        RequestDispatcher rd =
                request.getRequestDispatcher("faculty-dashboard.html");
        rd.forward(request, response);
    }
}
