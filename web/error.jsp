<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error | IoMT Cloud</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card" style="text-align: center;">
            <span style="font-size: 4rem; display: block; margin-bottom: 16px;">⚠️</span>
            <h1 style="color: var(--accent-rose); font-size: 2rem; margin-bottom: 8px;">Oops!</h1>
            <p style="color: var(--text-secondary); margin-bottom: 24px;">Something went wrong. The page you're looking for could not be found or an error occurred.</p>

            <% if (request.getAttribute("javax.servlet.error.status_code") != null) { %>
                <p style="color: var(--text-muted); font-size: 0.85rem;">
                    Error Code: <strong><%= request.getAttribute("javax.servlet.error.status_code") %></strong>
                </p>
            <% } %>

            <div class="mt-3">
                <a href="login" class="btn btn-primary">🏠 Go to Login</a>
                <a href="javascript:history.back()" class="btn btn-secondary" style="margin-left: 8px;">← Go Back</a>
            </div>
        </div>
    </div>
</body>
</html>
