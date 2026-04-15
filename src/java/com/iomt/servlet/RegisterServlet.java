package com.iomt.servlet;

import com.iomt.crypto.AESUtil;
import com.iomt.dao.UserDAO;
import com.iomt.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * RegisterServlet - Handles new user registration
 * GET: Display registration page
 * POST: Create new user account
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");
        String department = request.getParameter("department");
        String clearanceLevelStr = request.getParameter("clearanceLevel");

        // Validation
        if (name == null || email == null || password == null || role == null ||
            name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Check if email exists
        if (userDAO.emailExists(email)) {
            request.setAttribute("error", "Email already registered.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Parse clearance level
        int clearanceLevel = 1;
        try {
            clearanceLevel = Integer.parseInt(clearanceLevelStr);
            clearanceLevel = Math.max(1, Math.min(5, clearanceLevel));
        } catch (NumberFormatException e) {
            clearanceLevel = 1;
        }

        // Create user
        User user = new User(name, email, AESUtil.hashPassword(password), role,
                department != null ? department : "general", clearanceLevel);

        boolean success = userDAO.register(user);

        if (success) {
            request.setAttribute("success", "Registration successful! Please login.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}
