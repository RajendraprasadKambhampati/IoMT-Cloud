<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="IoMT Secure Cloud Storage - Login">
    <title>Login | IoMT Secure Cloud Storage</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <div class="logo">
                <span class="icon">🔐</span>
                <h1>IoMT Cloud</h1>
                <p>Trust-Aware Secure Federated Cloud Storage</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    ⚠️ <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    ✅ <%= request.getAttribute("success") %>
                </div>
            <% } %>

            <form action="login" method="post" onsubmit="return validateLoginForm()">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control"
                           placeholder="Enter your email" required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="Enter your password" required>
                </div>

                <button type="submit" class="btn btn-primary btn-block btn-lg">
                    🔓 Sign In
                </button>
            </form>

            <p class="text-center mt-3" style="color: var(--text-secondary); font-size: 0.9rem;">
                Don't have an account? <a href="register">Create Account</a>
            </p>

            <div class="mt-3 text-center" style="font-size: 0.75rem; color: var(--text-muted);">
                <p>Demo Credentials: admin@iomt.com / admin123</p>
            </div>
        </div>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
