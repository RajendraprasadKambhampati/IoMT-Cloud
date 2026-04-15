package com.iomt.servlet;

import com.iomt.dao.LogDAO;
import com.iomt.model.LogEntry;
import com.iomt.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * LogoutServlet - Handles user logout
 * Invalidates session and redirects to login
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                // Log the logout
                LogDAO logDAO = new LogDAO();
                LogEntry log = new LogEntry(user.getId(), "LOGOUT",
                        "User logged out", request.getRemoteAddr());
                logDAO.addLog(log);
            }
            session.invalidate();
        }
        response.sendRedirect("login");
    }
}
