package com.iomt.servlet;

import com.iomt.federated.FederatedLearning;
import com.iomt.model.FederatedUpdate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * FederatedServlet - Federated Learning simulation
 * GET: Show current rounds and results
 * POST: Run a new federated learning round
 */
@WebServlet("/federated")
public class FederatedServlet extends HttpServlet {

    private FederatedLearning federated = new FederatedLearning();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        // Get latest round number
        int latestRound = federated.getLatestRound();

        // Get all updates
        List<FederatedUpdate> allUpdates = federated.getAllUpdates();

        // Get updates for the latest round
        List<FederatedUpdate> latestUpdates = null;
        if (latestRound > 0) {
            latestUpdates = federated.getUpdatesByRound(latestRound);
        }

        request.setAttribute("latestRound", latestRound);
        request.setAttribute("allUpdates", allUpdates);
        request.setAttribute("latestUpdates", latestUpdates);
        request.setAttribute("deviceIds", FederatedLearning.getDeviceIds());

        request.getRequestDispatcher("federated.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");

        if ("runRound".equals(action)) {
            // Run a new federated learning round
            List<FederatedUpdate> roundUpdates = federated.runRound();
            request.setAttribute("success",
                    "Federated learning round completed! " + roundUpdates.size() + " device updates aggregated.");
        }

        // Reload and forward
        doGet(request, response);
    }
}
