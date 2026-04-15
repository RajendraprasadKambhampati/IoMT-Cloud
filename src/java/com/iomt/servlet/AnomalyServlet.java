package com.iomt.servlet;

import com.iomt.dao.LogDAO;
import com.iomt.model.LogEntry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * AnomalyServlet - Anomaly detection dashboard
 * Shows flagged anomalies and suspicious activities
 */
@WebServlet("/anomaly")
public class AnomalyServlet extends HttpServlet {

    private LogDAO logDAO = new LogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        // Get anomaly logs
        List<LogEntry> anomalyLogs = logDAO.getAnomalyLogs();
        int anomalyCount = logDAO.getAnomalyCount();

        // Get all recent logs
        List<LogEntry> recentLogs = logDAO.getRecentLogs(50);

        request.setAttribute("anomalyLogs", anomalyLogs);
        request.setAttribute("anomalyCount", anomalyCount);
        request.setAttribute("recentLogs", recentLogs);

        request.getRequestDispatcher("anomaly.jsp").forward(request, response);
    }
}
