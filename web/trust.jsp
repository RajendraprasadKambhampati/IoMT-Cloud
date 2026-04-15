<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.iomt.model.*, com.iomt.trust.TrustManager, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect("login"); return; }
    List<User> users = (List<User>) request.getAttribute("users");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trust Scores | IoMT Cloud</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <button class="mobile-toggle" onclick="toggleSidebar()">☰</button>
    <div class="sidebar-overlay" onclick="toggleSidebar()"></div>

    <div class="app-layout">
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>🔐 IoMT Cloud</h2>
                <div class="subtitle">Secure Federated Storage</div>
            </div>
            <nav class="sidebar-nav">
                <div class="nav-section">
                    <div class="nav-section-title">Main</div>
                    <a href="<%= "admin".equals(user.getRole()) ? "admin-dashboard" : "dashboard" %>" class="nav-link"><span class="icon">📊</span> Dashboard</a>
                    <a href="upload" class="nav-link"><span class="icon">📤</span> Upload File</a>
                    <a href="files" class="nav-link"><span class="icon">📁</span> My Files</a>
                    <a href="access-request" class="nav-link"><span class="icon">🔑</span> Access Requests</a>
                </div>
                <div class="nav-section">
                    <div class="nav-section-title">Security</div>
                    <a href="blockchain" class="nav-link"><span class="icon">⛓️</span> Blockchain</a>
                    <a href="anomaly" class="nav-link"><span class="icon">🚨</span> Anomaly Alerts</a>
                    <a href="trust" class="nav-link active"><span class="icon">🛡️</span> Trust Scores</a>
                </div>
                <div class="nav-section">
                    <div class="nav-section-title">AI / ML</div>
                    <a href="federated" class="nav-link"><span class="icon">🤝</span> Federated Learning</a>
                </div>
            </nav>
            <div class="sidebar-footer">
                <div class="user-info">
                    <div class="user-avatar"><%= user.getName().substring(0, 1).toUpperCase() %></div>
                    <div>
                        <div class="user-name"><%= user.getName() %></div>
                        <div class="user-role"><%= user.getRole() %></div>
                    </div>
                </div>
                <a href="logout" class="btn btn-secondary btn-sm btn-block">🚪 Logout</a>
            </div>
        </aside>

        <main class="main-content">
            <div class="page-header" style="display: flex; justify-content: space-between; align-items: flex-start;">
                <div>
                    <h1>Trust Score Dashboard 🛡️</h1>
                    <p>Dynamic trust management based on user behavior</p>
                </div>
                <% if ("admin".equals(user.getRole())) { %>
                <form action="trust" method="post">
                    <input type="hidden" name="action" value="recalculate">
                    <button type="submit" class="btn btn-primary">🔄 Recalculate All</button>
                </form>
                <% } %>
            </div>

            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">✅ <%= request.getAttribute("success") %></div>
            <% } %>

            <!-- Trust Level Legend -->
            <div class="card mb-3">
                <div class="card-header">
                    <h3>📊 Trust Levels</h3>
                </div>
                <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px;">
                    <div style="padding: 16px; background: rgba(16, 185, 129, 0.1); border-radius: var(--radius-sm); text-align: center; border: 1px solid rgba(16, 185, 129, 0.3);">
                        <h4 class="trust-high" style="font-size: 1.5rem;">80-100</h4>
                        <p style="font-size: 0.8rem; color: var(--accent-emerald);">HIGH</p>
                        <p style="font-size: 0.7rem; color: var(--text-muted);">Full Access</p>
                    </div>
                    <div style="padding: 16px; background: rgba(245, 158, 11, 0.1); border-radius: var(--radius-sm); text-align: center; border: 1px solid rgba(245, 158, 11, 0.3);">
                        <h4 class="trust-medium" style="font-size: 1.5rem;">50-79</h4>
                        <p style="font-size: 0.8rem; color: var(--accent-amber);">MEDIUM</p>
                        <p style="font-size: 0.7rem; color: var(--text-muted);">Standard Access</p>
                    </div>
                    <div style="padding: 16px; background: rgba(244, 63, 94, 0.1); border-radius: var(--radius-sm); text-align: center; border: 1px solid rgba(244, 63, 94, 0.3);">
                        <h4 class="trust-low" style="font-size: 1.5rem;">30-49</h4>
                        <p style="font-size: 0.8rem; color: var(--accent-rose);">LOW</p>
                        <p style="font-size: 0.7rem; color: var(--text-muted);">Restricted</p>
                    </div>
                    <div style="padding: 16px; background: rgba(239, 68, 68, 0.1); border-radius: var(--radius-sm); text-align: center; border: 1px solid rgba(239, 68, 68, 0.3);">
                        <h4 class="trust-blocked" style="font-size: 1.5rem;">0-29</h4>
                        <p style="font-size: 0.8rem; color: #ef4444;">BLOCKED</p>
                        <p style="font-size: 0.7rem; color: var(--text-muted);">No Access</p>
                    </div>
                </div>
            </div>

            <!-- User Trust Scores -->
            <div class="card">
                <div class="card-header">
                    <h3>👥 User Trust Scores</h3>
                </div>
                <% if (users != null && !users.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>User</th><th>Role</th><th>Department</th>
                                <th>Trust Score</th><th>Level</th><th>Status</th>
                                <% if ("admin".equals(user.getRole())) { %><th>Actions</th><% } %>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (User u : users) {
                                String level = TrustManager.getTrustLevel(u.getTrustScore());
                                String tClass = TrustManager.getTrustLevelClass(u.getTrustScore());
                            %>
                            <tr>
                                <td><strong><%= u.getName() %></strong><br><span style="font-size: 0.75rem; color: var(--text-muted);"><%= u.getEmail() %></span></td>
                                <td><span class="badge badge-primary"><%= u.getRole() %></span></td>
                                <td><%= u.getDepartment() %></td>
                                <td>
                                    <span class="<%= tClass %>" style="font-size: 1.2rem; font-weight: 800;">
                                        <%= String.format("%.1f", u.getTrustScore()) %>
                                    </span>
                                    <div class="trust-bar">
                                        <div class="trust-bar-fill <%= u.getTrustScore() >= 80 ? "high" : u.getTrustScore() >= 50 ? "medium" : u.getTrustScore() >= 30 ? "low" : "blocked" %>"
                                             style="width: <%= u.getTrustScore() %>%"></div>
                                    </div>
                                </td>
                                <td>
                                    <span class="badge badge-<%= "HIGH".equals(level) ? "success" : "MEDIUM".equals(level) ? "warning" : "danger" %>">
                                        🛡️ <%= level %>
                                    </span>
                                </td>
                                <td>
                                    <span class="badge badge-<%= "active".equals(u.getStatus()) ? "success" : "blocked".equals(u.getStatus()) ? "danger" : "warning" %>">
                                        <%= u.getStatus() %>
                                    </span>
                                </td>
                                <% if ("admin".equals(user.getRole())) { %>
                                <td>
                                    <form action="trust" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="recalculateOne">
                                        <input type="hidden" name="userId" value="<%= u.getId() %>">
                                        <button type="submit" class="btn btn-secondary btn-sm">🔄 Recalculate</button>
                                    </form>
                                </td>
                                <% } %>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                    <div class="empty-state">
                        <span class="icon">👥</span>
                        <h3>No users found</h3>
                    </div>
                <% } %>
            </div>
        </main>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
