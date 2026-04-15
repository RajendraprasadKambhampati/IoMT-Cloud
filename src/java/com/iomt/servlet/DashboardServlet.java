package com.iomt.servlet;

import com.iomt.dao.*;
import com.iomt.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * DashboardServlet - User dashboard
 * Shows user stats, recent activity, trust score, file overview
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Refresh user data from DB
        UserDAO userDAO = new UserDAO();
        user = userDAO.getUserById(user.getId());
        session.setAttribute("user", user);

        // Get stats
        FileDAO fileDAO = new FileDAO();
        LogDAO logDAO = new LogDAO();
        AccessRequestDAO accessRequestDAO = new AccessRequestDAO();
        BlockchainDAO blockchainDAO = new BlockchainDAO();

        int fileCount = fileDAO.getFileCountByOwner(user.getId());
        List<FileRecord> recentFiles = fileDAO.getFilesByOwner(user.getId());
        List<LogEntry> recentLogs = logDAO.getRecentLogs(10);
        int pendingRequests = accessRequestDAO.getPendingCount(user.getId());
        int totalBlocks = blockchainDAO.getBlockCount();

        // Set attributes for JSP
        request.setAttribute("fileCount", fileCount);
        request.setAttribute("recentFiles", recentFiles);
        request.setAttribute("recentLogs", recentLogs);
        request.setAttribute("pendingRequests", pendingRequests);
        request.setAttribute("totalBlocks", totalBlocks);
        request.setAttribute("trustLevel", com.iomt.trust.TrustManager.getTrustLevel(user.getTrustScore()));
        request.setAttribute("trustClass", com.iomt.trust.TrustManager.getTrustLevelClass(user.getTrustScore()));

        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}
