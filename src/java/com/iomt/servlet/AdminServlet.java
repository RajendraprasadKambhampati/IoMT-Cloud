package com.iomt.servlet;

import com.iomt.dao.*;
import com.iomt.model.*;
import com.iomt.trust.TrustManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * AdminServlet - Admin dashboard and management
 * Provides system overview, user management, and monitoring
 */
@WebServlet("/admin-dashboard")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            response.sendRedirect("dashboard");
            return;
        }

        // Gather all stats
        UserDAO userDAO = new UserDAO();
        FileDAO fileDAO = new FileDAO();
        LogDAO logDAO = new LogDAO();
        BlockchainDAO blockchainDAO = new BlockchainDAO();

        // User stats
        int totalUsers = userDAO.getTotalUserCount();
        int doctorCount = userDAO.getUserCountByRole("doctor");
        int hospitalCount = userDAO.getUserCountByRole("hospital");
        int deviceCount = userDAO.getUserCountByRole("device");
        List<User> allUsers = userDAO.getAllUsers();

        // File stats
        int totalFiles = fileDAO.getTotalFileCount();
        List<FileRecord> recentFiles = fileDAO.getAllFiles();

        // Log stats
        int totalLogs = logDAO.getTotalLogCount();
        int anomalyCount = logDAO.getAnomalyCount();
        List<LogEntry> recentLogs = logDAO.getRecentLogs(20);
        List<LogEntry> anomalyLogs = logDAO.getAnomalyLogs();

        // Blockchain stats
        int blockCount = blockchainDAO.getBlockCount();

        // Set all attributes
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("doctorCount", doctorCount);
        request.setAttribute("hospitalCount", hospitalCount);
        request.setAttribute("deviceCount", deviceCount);
        request.setAttribute("allUsers", allUsers);
        request.setAttribute("totalFiles", totalFiles);
        request.setAttribute("recentFiles", recentFiles);
        request.setAttribute("totalLogs", totalLogs);
        request.setAttribute("anomalyCount", anomalyCount);
        request.setAttribute("recentLogs", recentLogs);
        request.setAttribute("anomalyLogs", anomalyLogs);
        request.setAttribute("blockCount", blockCount);

        request.getRequestDispatcher("admin-dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User admin = (User) session.getAttribute("user");
        if (!"admin".equals(admin.getRole())) {
            response.sendRedirect("dashboard");
            return;
        }

        String action = request.getParameter("action");
        UserDAO userDAO = new UserDAO();

        if ("blockUser".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            userDAO.updateStatus(userId, "blocked");
            userDAO.updateTrustScore(userId, 0);
            request.setAttribute("success", "User blocked successfully.");
        } else if ("activateUser".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            userDAO.updateStatus(userId, "active");
            userDAO.updateTrustScore(userId, 50); // Reset to medium trust
            request.setAttribute("success", "User activated successfully.");
        } else if ("restrictUser".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            userDAO.updateStatus(userId, "restricted");
            request.setAttribute("success", "User restricted successfully.");
        }

        doGet(request, response);
    }
}
