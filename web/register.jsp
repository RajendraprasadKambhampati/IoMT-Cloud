<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="IoMT Secure Cloud Storage - Register">
    <title>Register | IoMT Secure Cloud Storage</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card" style="max-width: 520px;">
            <div class="logo">
                <span class="icon">👤</span>
                <h1>Create Account</h1>
                <p>Join the IoMT Secure Cloud Platform</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    ⚠️ <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <form action="register" method="post" onsubmit="return validateRegisterForm()">
                <div class="form-group">
                    <label for="name">Full Name</label>
                    <input type="text" id="name" name="name" class="form-control"
                           placeholder="Enter your full name" required>
                </div>

                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control"
                           placeholder="Enter your email" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" class="form-control"
                               placeholder="Min 6 characters" required>
                    </div>
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                               placeholder="Confirm password" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="role">Role</label>
                    <select id="role" name="role" class="form-control" required>
                        <option value="">Select your role</option>
                        <option value="doctor">👨‍⚕️ Doctor</option>
                        <option value="hospital">🏥 Hospital</option>
                        <option value="device">📟 IoMT Device</option>
                    </select>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="department">Department</label>
                        <select id="department" name="department" class="form-control">
                            <option value="general">General</option>
                            <option value="cardiology">Cardiology</option>
                            <option value="neurology">Neurology</option>
                            <option value="orthopedics">Orthopedics</option>
                            <option value="pediatrics">Pediatrics</option>
                            <option value="radiology">Radiology</option>
                            <option value="oncology">Oncology</option>
                            <option value="emergency">Emergency</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="clearanceLevel">Clearance Level</label>
                        <select id="clearanceLevel" name="clearanceLevel" class="form-control">
                            <option value="1">Level 1 (Basic)</option>
                            <option value="2">Level 2 (Standard)</option>
                            <option value="3">Level 3 (Advanced)</option>
                            <option value="4">Level 4 (Senior)</option>
                            <option value="5">Level 5 (Admin)</option>
                        </select>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-block btn-lg">
                    ✨ Create Account
                </button>
            </form>

            <p class="text-center mt-3" style="color: var(--text-secondary); font-size: 0.9rem;">
                Already have an account? <a href="login">Sign In</a>
            </p>
        </div>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
