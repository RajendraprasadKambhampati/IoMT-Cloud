package com.iomt.servlet;

import com.iomt.crypto.AESUtil;
import com.iomt.dao.UserDAO;
import com.iomt.dao.LogDAO;
import com.iomt.model.User;
import com.iomt.model.LogEntry;
import com.iomt.anomaly.AnomalyDetector;
import com.iomt.trust.TrustManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * LoginServlet - Handles user authentication
 * GET: Display login page
 * POST: Authenticate user credentials
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private AnomalyDetector anomalyDetector = new AnomalyDetector();
    private TrustManager trustManager = new TrustManager();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already logged in, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            if ("admin".equals(user.getRole())) {
                response.sendRedirect("admin-dashboard");
            } else {
                response.sendRedirect("dashboard");
            }
            return;
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Please enter both email and password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Hash the password
        String hashedPassword = AESUtil.hashPassword(password);

        // Authenticate
        User user = userDAO.login(email, hashedPassword);

        if (user != null) {
            // Check if user is blocked
            if ("blocked".equals(user.getStatus())) {
                request.setAttribute("error", "Your account has been blocked due to low trust score. Contact admin.");
                // Log the attempt
                anomalyDetector.analyzeAndFlag(user.getId(), "LOGIN_BLOCKED",
                        "Blocked user login attempt", request.getRemoteAddr());
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            // Check trust score
            if (!TrustManager.checkAccessByTrust(user)) {
                request.setAttribute("error", "Account restricted due to low trust score (" +
                        String.format("%.0f", user.getTrustScore()) + "/100). Contact admin.");
                anomalyDetector.analyzeAndFlag(user.getId(), "LOGIN_LOW_TRUST",
                        "Low trust login attempt", request.getRemoteAddr());
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            // Successful login
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // Log successful login
            anomalyDetector.analyzeAndFlag(user.getId(), "LOGIN_SUCCESS",
                    "User logged in successfully", request.getRemoteAddr());

            // Update trust for successful login
            trustManager.updateTrust(user.getId(), "LOGIN_SUCCESS", true, false);

            // Redirect based on role
            if ("admin".equals(user.getRole())) {
                response.sendRedirect("admin-dashboard");
            } else {
                response.sendRedirect("dashboard");
            }
        } else {
            // Failed login - try to find user by email for anomaly tracking
            request.setAttribute("error", "Invalid email or password.");

            // Log failed attempt (try to find user by email first)
            LogDAO logDAO = new LogDAO();
            LogEntry log = new LogEntry(0, "LOGIN_FAILED",
                    "Failed login attempt for email: " + email, request.getRemoteAddr());
            logDAO.addLog(log);

            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
