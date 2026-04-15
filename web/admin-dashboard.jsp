<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.iomt.model.*, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"admin".equals(user.getRole())) { response.sendRedirect("login"); return; }

    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
    Integer doctorCount = (Integer) request.getAttribute("doctorCount");
    Integer hospitalCount = (Integer) request.getAttribute("hospitalCount");
    Integer deviceCount = (Integer) request.getAttribute("deviceCount");
    Integer totalFiles = (Integer) request.getAttribute("totalFiles");
    Integer totalLogs = (Integer) request.getAttribute("totalLogs");
    Integer anomalyCount = (Integer) request.getAttribute("anomalyCount");
    Integer blockCount = (Integer) request.getAttribute("blockCount");
    List<User> allUsers = (List<User>) request.getAttribute("allUsers");
    List<LogEntry> anomalyLogs = (List<LogEntry>) request.getAttribute("anomalyLogs");
    List<LogEntry> recentLogs = (List<LogEntry>) request.getAttribute("recentLogs");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard | IoMT Cloud</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <button class="mobile-toggle" onclick="toggleSidebar()">☰</button>
    <div class="sidebar-overlay" onclick="toggleSidebar()"></div>

    <div class="app-layout">
        <!-- Admin Sidebar -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>🔐 IoMT Cloud</h2>
                <div class="subtitle">Admin Control Panel</div>
            </div>
            <nav class="sidebar-nav">
                <div class="nav-section">
                    <div class="nav-section-title">Admin</div>
                    <a href="admin-dashboard" class="nav-link active"><span class="icon">📊</span> Dashboard</a>
                </div>
                <div class="nav-section">
                    <div class="nav-section-title">Data</div>
                    <a href="upload" class="nav-link"><span class="icon">📤</span> Upload File</a>
                    <a href="files" class="nav-link"><span class="icon">📁</span> All Files</a>
                    <a href="access-request" class="nav-link"><span class="icon">🔑</span> Access Requests</a>
                </div>
                <div class="nav-section">
                    <div class="nav-section-title">Security</div>
                    <a href="blockchain" class="nav-link"><span class="icon">⛓️</span> Blockchain</a>
                    <a href="anomaly" class="nav-link"><span class="icon">🚨</span> Anomaly Alerts</a>
                    <a href="trust" class="nav-link"><span class="icon">🛡️</span> Trust Scores</a>
                </div>
                <div class="nav-section">
                    <div class="nav-section-title">AI / ML</div>
                    <a href="federated" class="nav-link"><span class="icon">🤝</span> Federated Learning</a>
                </div>
            </nav>
            <div class="sidebar-footer">
                <div class="user-info">
                    <div class="user-avatar" style="background: linear-gradient(135deg, #f43f5e, #e11d48);">A</div>
                    <div>
                        <div class="user-name"><%= user.getName() %></div>
                        <div class="user-role">Administrator</div>
                    </div>
                </div>
                <a href="logout" class="btn btn-secondary btn-sm btn-block">🚪 Logout</a>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <div class="page-header">
                <h1>Admin Dashboard 🎛️</h1>
                <p>System overview and management</p>
            </div>

            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">✅ <%= request.getAttribute("success") %></div>
            <% } %>

            <!-- Stats Grid -->
            <div class="stats-grid">
                <div class="stat-card primary">
                    <div class="stat-icon">👥</div>
                    <div class="stat-info">
                        <h3><%= totalUsers != null ? totalUsers : 0 %></h3>
                        <p>Total Users</p>
                    </div>
                </div>
                <div class="stat-card cyan">
                    <div class="stat-icon">📁</div>
                    <div class="stat-info">
                        <h3><%= totalFiles != null ? totalFiles : 0 %></h3>
                        <p>Encrypted Files</p>
                    </div>
                </div>
                <div class="stat-card emerald">
                    <div class="stat-icon">⛓️</div>
                    <div class="stat-info">
                        <h3><%= blockCount != null ? blockCount : 0 %></h3>
                        <p>Blockchain Blocks</p>
                    </div>
                </div>
                <div class="stat-card rose">
                    <div class="stat-icon">🚨</div>
                    <div class="stat-info">
                        <h3><%= anomalyCount != null ? anomalyCount : 0 %></h3>
                        <p>Anomalies Detected</p>
                    </div>
                </div>
                <div class="stat-card amber">
                    <div class="stat-icon">👨‍⚕️</div>
                    <div class="stat-info">
                        <h3><%= doctorCount != null ? doctorCount : 0 %></h3>
                        <p>Doctors</p>
                    </div>
                </div>
                <div class="stat-card violet">
                    <div class="stat-icon">📟</div>
                    <div class="stat-info">
                        <h3><%= deviceCount != null ? deviceCount : 0 %></h3>
                        <p>IoMT Devices</p>
                    </div>
                </div>
            </div>

            <!-- User Management -->
            <div class="card mb-3">
                <div class="card-header">
                    <h3>👥 User Management</h3>
                </div>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th><th>Name</th><th>Email</th><th>Role</th>
                                <th>Department</th><th>Trust</th><th>Status</th><th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (allUsers != null) { for (User u : allUsers) { %>
                            <tr>
                                <td><%= u.getId() %></td>
                                <td><strong><%= u.getName() %></strong></td>
                                <td><%= u.getEmail() %></td>
                                <td><span class="badge badge-primary"><%= u.getRole() %></span></td>
                                <td><%= u.getDepartment() %></td>
                                <td>
                                    <span class="<%= u.getTrustScore() >= 80 ? "trust-high" : u.getTrustScore() >= 50 ? "trust-medium" : "trust-low" %>"
                                          style="font-weight: 700;">
                                        <%= String.format("%.0f", u.getTrustScore()) %>
                                    </span>
                                </td>
                                <td>
                                    <span class="badge badge-<%= "active".equals(u.getStatus()) ? "success" : "blocked".equals(u.getStatus()) ? "danger" : "warning" %>">
                                        <%= u.getStatus() %>
                                    </span>
                                </td>
                                <td>
                                    <% if (!"admin".equals(u.getRole())) { %>
                                    <div class="action-group">
                                        <% if (!"blocked".equals(u.getStatus())) { %>
                                        <form action="admin-dashboard" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="blockUser">
                                            <input type="hidden" name="userId" value="<%= u.getId() %>">
                                            <button type="submit" class="btn btn-danger btn-sm"
                                                    onclick="return confirmAction('Block this user?')">Block</button>
                                        </form>
                                        <% } %>
                                        <% if (!"active".equals(u.getStatus())) { %>
                                        <form action="admin-dashboard" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="activateUser">
                                            <input type="hidden" name="userId" value="<%= u.getId() %>">
                                            <button type="submit" class="btn btn-success btn-sm">Activate</button>
                                        </form>
                                        <% } %>
                                    </div>
                                    <% } else { %>
                                        <span style="color: var(--text-muted);">—</span>
                                    <% } %>
                                </td>
                            </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Anomaly Alerts -->
            <div class="card mb-3">
                <div class="card-header">
                    <h3>🚨 Recent Anomalies</h3>
                    <a href="anomaly" class="btn btn-sm btn-secondary">View All</a>
                </div>
                <% if (anomalyLogs != null && !anomalyLogs.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr><th>Time</th><th>User</th><th>Action</th><th>Details</th><th>Severity</th></tr>
                        </thead>
                        <tbody>
                            <% int ac = 0; for (LogEntry l : anomalyLogs) { if (ac++ >= 10) break; %>
                            <tr>
                                <td style="font-size: 0.8rem;"><%= l.getTimestamp() %></td>
                                <td><%= l.getUserName() != null ? l.getUserName() : "Unknown" %></td>
                                <td><%= l.getAction() %></td>
                                <td style="font-size: 0.8rem; max-width: 300px; overflow: hidden; text-overflow: ellipsis;">
                                    <%= l.getDetails() != null ? l.getDetails() : "" %>
                                </td>
                                <td><span class="badge badge-danger"><%= l.getSeverity() %></span></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                    <div class="empty-state">
                        <span class="icon">✅</span>
                        <h3>No anomalies detected</h3>
                        <p>System is operating normally</p>
                    </div>
                <% } %>
            </div>

            <!-- Recent Logs -->
            <div class="card">
                <div class="card-header">
                    <h3>📋 System Logs</h3>
                </div>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr><th>Time</th><th>User</th><th>Action</th><th>IP</th><th>Status</th></tr>
                        </thead>
                        <tbody>
                            <% if (recentLogs != null) { int lc = 0; for (LogEntry l : recentLogs) { if (lc++ >= 15) break; %>
                            <tr>
                                <td style="font-size: 0.8rem;"><%= l.getTimestamp() %></td>
                                <td><%= l.getUserName() != null ? l.getUserName() : "System" %></td>
                                <td><%= l.getAction() %></td>
                                <td style="font-size: 0.8rem;"><%= l.getIpAddress() != null ? l.getIpAddress() : "—" %></td>
                                <td>
                                    <% if (l.isAnomalyFlag()) { %>
                                        <span class="badge badge-danger">⚠️ Anomaly</span>
                                    <% } else { %>
                                        <span class="badge badge-success">✓</span>
                                    <% } %>
                                </td>
                            </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
