package com.event;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class GetStudentEmailServlet
 */
@WebServlet("/GetStudentEmailServlet")
public class GetStudentEmailServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        HttpSession session = req.getSession(false);

        if (session == null) {
            res.getWriter().print("");
            return;
        }

        Object email = session.getAttribute("studentEmail");
        res.getWriter().print(email == null ? "" : email.toString());
    }
}
