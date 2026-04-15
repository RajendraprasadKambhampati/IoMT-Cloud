<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.iomt.model.*, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect("login"); return; }
    List<LogEntry> anomalyLogs = (List<LogEntry>) request.getAttribute("anomalyLogs");
    Integer anomalyCount = (Integer) request.getAttribute("anomalyCount");
    List<LogEntry> recentLogs = (List<LogEntry>) request.getAttribute("recentLogs");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Anomaly Detection | IoMT Cloud</title>
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
                    <a href="anomaly" class="nav-link active"><span class="icon">🚨</span> Anomaly Alerts</a>
                    <a href="trust" class="nav-link"><span class="icon">🛡️</span> Trust Scores</a>
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
            <div class="page-header">
                <h1>Anomaly Detection 🚨</h1>
                <p>Real-time security monitoring and threat detection</p>
            </div>

            <!-- Stats -->
            <div class="stats-grid">
                <div class="stat-card rose">
                    <div class="stat-icon">🚨</div>
                    <div class="stat-info">
                        <h3><%= anomalyCount != null ? anomalyCount : 0 %></h3>
                        <p>Total Anomalies</p>
                    </div>
                </div>
                <div class="stat-card amber">
                    <div class="stat-icon">⚠️</div>
                    <div class="stat-info">
                        <h3>Active</h3>
                        <p>Detection Status</p>
                    </div>
                </div>
            </div>

            <!-- Detection Rules Info -->
            <div class="card mb-3">
                <div class="card-header">
                    <h3>📋 Detection Rules</h3>
                </div>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 16px;">
                    <div style="padding: 12px; background: var(--bg-glass); border-radius: var(--radius-sm);">
                        <strong style="color: var(--accent-rose);">🔒 Brute Force</strong>
                        <p style="font-size: 0.8rem; color: var(--text-secondary); margin-top: 4px;">3+ failed logins within 10 minutes</p>
                    </div>
                    <div style="padding: 12px; background: var(--bg-glass); border-radius: var(--radius-sm);">
                        <strong style="color: var(--accent-amber);">📊 Data Exfiltration</strong>
                        <p style="font-size: 0.8rem; color: var(--text-secondary); margin-top: 4px;">10+ file accesses within 5 minutes</p>
                    </div>
                    <div style="padding: 12px; background: var(--bg-glass); border-radius: var(--radius-sm);">
                        <strong style="color: var(--accent-violet);">🔐 Policy Violation</strong>
                        <p style="font-size: 0.8rem; color: var(--text-secondary); margin-top: 4px;">Unauthorized resource access attempts</p>
                    </div>
                    <div style="padding: 12px; background: var(--bg-glass); border-radius: var(--radius-sm);">
                        <strong style="color: var(--accent-cyan);">👤 Account Abuse</strong>
                        <p style="font-size: 0.8rem; color: var(--text-secondary); margin-top: 4px;">Blocked/restricted user activity</p>
                    </div>
                </div>
            </div>

            <!-- Anomaly Alerts Table -->
            <div class="card mb-3">
                <div class="card-header">
                    <h3>⚠️ Flagged Anomalies</h3>
                </div>
                <% if (anomalyLogs != null && !anomalyLogs.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr><th>Time</th><th>User</th><th>Action</th><th>Details</th><th>IP</th><th>Severity</th></tr>
                        </thead>
                        <tbody>
                            <% for (LogEntry l : anomalyLogs) { %>
                            <tr style="background: rgba(244, 63, 94, 0.05);">
                                <td style="font-size: 0.8rem;"><%= l.getTimestamp() %></td>
                                <td><strong><%= l.getUserName() != null ? l.getUserName() : "Unknown" %></strong></td>
                                <td><span class="badge badge-danger"><%= l.getAction() %></span></td>
                                <td style="font-size: 0.8rem; max-width: 400px; overflow: hidden; text-overflow: ellipsis;">
                                    <%= l.getDetails() != null ? l.getDetails() : "—" %>
                                </td>
                                <td style="font-size: 0.8rem;"><%= l.getIpAddress() != null ? l.getIpAddress() : "—" %></td>
                                <td>
                                    <span class="badge badge-<%= "HIGH".equals(l.getSeverity()) ? "danger" : "warning" %>">
                                        <%= l.getSeverity() %>
                                    </span>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                    <div class="empty-state">
                        <span class="icon">✅</span>
                        <h3>No anomalies detected</h3>
                        <p>System is operating securely</p>
                    </div>
                <% } %>
            </div>

            <!-- All Recent Logs -->
            <div class="card">
                <div class="card-header">
                    <h3>📋 All Recent Activity</h3>
                </div>
                <% if (recentLogs != null && !recentLogs.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr><th>Time</th><th>User</th><th>Action</th><th>Status</th></tr>
                        </thead>
                        <tbody>
                            <% for (LogEntry l : recentLogs) { %>
                            <tr>
                                <td style="font-size: 0.8rem;"><%= l.getTimestamp() %></td>
                                <td><%= l.getUserName() != null ? l.getUserName() : "System" %></td>
                                <td><%= l.getAction() %></td>
                                <td>
                                    <% if (l.isAnomalyFlag()) { %>
                                        <span class="badge badge-danger">⚠️ Flagged</span>
                                    <% } else { %>
                                        <span class="badge badge-success">✓ Normal</span>
                                    <% } %>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } %>
            </div>
        </main>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
