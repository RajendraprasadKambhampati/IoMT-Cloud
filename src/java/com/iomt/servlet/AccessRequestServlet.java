package com.iomt.servlet;

import com.iomt.dao.AccessRequestDAO;
import com.iomt.model.AccessRequest;
import com.iomt.model.User;
import com.iomt.anomaly.AnomalyDetector;
import com.iomt.blockchain.Blockchain;
import com.iomt.trust.TrustManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * AccessRequestServlet - Manages file access requests
 * GET: Show pending requests
 * POST: Create, approve, or deny requests
 */
@WebServlet("/access-request")
public class AccessRequestServlet extends HttpServlet {

    private AccessRequestDAO accessRequestDAO = new AccessRequestDAO();
    private AnomalyDetector anomalyDetector = new AnomalyDetector();
    private Blockchain blockchain = new Blockchain();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Get pending requests for files owned by this user
        List<AccessRequest> pendingRequests = accessRequestDAO.getPendingRequestsForOwner(user.getId());
        // Get requests made by this user
        List<AccessRequest> myRequests = accessRequestDAO.getRequestsByUser(user.getId());

        request.setAttribute("pendingRequests", pendingRequests);
        request.setAttribute("myRequests", myRequests);

        request.getRequestDispatcher("access-requests.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        if ("request".equals(action)) {
            // Create new access request
            String fileIdStr = request.getParameter("fileId");
            if (fileIdStr != null) {
                int fileId = Integer.parseInt(fileIdStr);
                AccessRequest ar = new AccessRequest(user.getId(), fileId);
                boolean success = accessRequestDAO.createRequest(ar);
                if (success) {
                    anomalyDetector.analyzeAndFlag(user.getId(), "ACCESS_REQUEST",
                            "Requested access to file ID: " + fileId, request.getRemoteAddr());
                    request.setAttribute("success", "Access request submitted successfully.");
                } else {
                    request.setAttribute("error", "Request already exists or failed.");
                }
            }
        } else if ("approve".equals(action) || "deny".equals(action)) {
            // Approve or deny a request
            String requestIdStr = request.getParameter("requestId");
            if (requestIdStr != null) {
                int requestId = Integer.parseInt(requestIdStr);
                String status = "approve".equals(action) ? "approved" : "denied";
                accessRequestDAO.updateRequestStatus(requestId, status);

                // Log to blockchain
                blockchain.addBlock(String.format(
                    "{\"event\":\"ACCESS_%s\",\"requestId\":%d,\"resolvedBy\":\"%s\",\"timestamp\":\"%s\"}",
                    status.toUpperCase(), requestId, user.getName(),
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
                ));

                request.setAttribute("success", "Request " + status + " successfully.");
            }
        }

        // Reload and forward
        doGet(request, response);
    }
}
