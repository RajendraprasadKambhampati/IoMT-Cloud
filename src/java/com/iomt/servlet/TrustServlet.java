package com.iomt.servlet;

import com.iomt.dao.UserDAO;
import com.iomt.model.User;
import com.iomt.trust.TrustManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * TrustServlet - Trust score dashboard
 * Shows trust scores for all users with recalculation capability
 */
@WebServlet("/trust")
public class TrustServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private TrustManager trustManager = new TrustManager();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        // Get all users with their trust scores
        List<User> users = userDAO.getAllUsers();

        request.setAttribute("users", users);
        request.getRequestDispatcher("trust.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        if ("recalculate".equals(action)) {
            // Admin can recalculate all trust scores
            if ("admin".equals(currentUser.getRole())) {
                List<User> users = userDAO.getAllUsers();
                for (User u : users) {
                    trustManager.recalculateTrust(u.getId());
                }
                request.setAttribute("success", "Trust scores recalculated for all users.");
            }
        } else if ("recalculateOne".equals(action)) {
            String userIdStr = request.getParameter("userId");
            if (userIdStr != null) {
                int userId = Integer.parseInt(userIdStr);
                double newScore = trustManager.recalculateTrust(userId);
                request.setAttribute("success", "Trust score recalculated: " + String.format("%.1f", newScore));
            }
        }

        doGet(request, response);
    }
}
