<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.iomt.model.*, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect("login"); return; }

    Integer fileCount = (Integer) request.getAttribute("fileCount");
    Integer pendingRequests = (Integer) request.getAttribute("pendingRequests");
    Integer totalBlocks = (Integer) request.getAttribute("totalBlocks");
    String trustLevel = (String) request.getAttribute("trustLevel");
    String trustClass = (String) request.getAttribute("trustClass");
    List<FileRecord> recentFiles = (List<FileRecord>) request.getAttribute("recentFiles");
    List<LogEntry> recentLogs = (List<LogEntry>) request.getAttribute("recentLogs");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | IoMT Cloud</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <button class="mobile-toggle" onclick="toggleSidebar()">☰</button>
    <div class="sidebar-overlay" onclick="toggleSidebar()"></div>

    <div class="app-layout">
        <!-- Sidebar -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>🔐 IoMT Cloud</h2>
                <div class="subtitle">Secure Federated Storage</div>
            </div>
            <nav class="sidebar-nav">
                <div class="nav-section">
                    <div class="nav-section-title">Main</div>
                    <a href="dashboard" class="nav-link active"><span class="icon">📊</span> Dashboard</a>
                    <a href="upload" class="nav-link"><span class="icon">📤</span> Upload File</a>
                    <a href="files" class="nav-link"><span class="icon">📁</span> My Files</a>
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
                    <div class="user-avatar"><%= user.getName().substring(0, 1).toUpperCase() %></div>
                    <div>
                        <div class="user-name"><%= user.getName() %></div>
                        <div class="user-role"><%= user.getRole() %></div>
                    </div>
                </div>
                <a href="logout" class="btn btn-secondary btn-sm btn-block">🚪 Logout</a>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <div class="page-header">
                <h1>Welcome back, <%= user.getName() %> 👋</h1>
                <p>Here's your dashboard overview</p>
            </div>

            <!-- Stats Grid -->
            <div class="stats-grid">
                <div class="stat-card primary">
                    <div class="stat-icon">📁</div>
                    <div class="stat-info">
                        <h3><%= fileCount != null ? fileCount : 0 %></h3>
                        <p>My Files</p>
                    </div>
                </div>
                <div class="stat-card cyan">
                    <div class="stat-icon">🛡️</div>
                    <div class="stat-info">
                        <h3 class="<%= trustClass %>"><%= String.format("%.0f", user.getTrustScore()) %></h3>
                        <p>Trust Score (<%= trustLevel %>)</p>
                    </div>
                </div>
                <div class="stat-card amber">
                    <div class="stat-icon">🔑</div>
                    <div class="stat-info">
                        <h3><%= pendingRequests != null ? pendingRequests : 0 %></h3>
                        <p>Pending Requests</p>
                    </div>
                </div>
                <div class="stat-card emerald">
                    <div class="stat-icon">⛓️</div>
                    <div class="stat-info">
                        <h3><%= totalBlocks != null ? totalBlocks : 0 %></h3>
                        <p>Blockchain Blocks</p>
                    </div>
                </div>
            </div>

            <!-- Trust Score Bar -->
            <div class="card mb-3">
                <div class="card-header">
                    <h3>🛡️ Your Trust Score</h3>
                    <span class="badge badge-<%= user.getTrustScore() >= 80 ? "success" : user.getTrustScore() >= 50 ? "warning" : "danger" %>">
                        <%= trustLevel %>
                    </span>
                </div>
                <div style="display: flex; align-items: center; gap: 16px;">
                    <div style="flex: 1;">
                        <div class="trust-bar">
                            <div class="trust-bar-fill <%= user.getTrustScore() >= 80 ? "high" : user.getTrustScore() >= 50 ? "medium" : user.getTrustScore() >= 30 ? "low" : "blocked" %>"
                                 style="width: <%= user.getTrustScore() %>%"></div>
                        </div>
                    </div>
                    <span style="font-size: 1.5rem; font-weight: 700;" class="<%= trustClass %>">
                        <%= String.format("%.1f", user.getTrustScore()) %>%
                    </span>
                </div>
                <p class="mt-1" style="font-size: 0.8rem; color: var(--text-muted);">
                    Attributes: <code><%= user.getAttributes() %></code> | Status: <span class="badge badge-<%= "active".equals(user.getStatus()) ? "success" : "danger" %>"><%= user.getStatus() %></span>
                </p>
            </div>

            <!-- Content Grid -->
            <div class="content-grid-2">
                <!-- My Recent Files -->
                <div class="card">
                    <div class="card-header">
                        <h3>📁 Recent Files</h3>
                        <a href="files" class="btn btn-sm btn-secondary">View All</a>
                    </div>
                    <% if (recentFiles != null && !recentFiles.isEmpty()) { %>
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr><th>File</th><th>Size</th><th>Policy</th></tr>
                                </thead>
                                <tbody>
                                    <% int count = 0; for (FileRecord f : recentFiles) { if (count++ >= 5) break; %>
                                    <tr>
                                        <td><%= f.getOriginalFilename() %></td>
                                        <td><%= f.getFormattedSize() %></td>
                                        <td><span class="badge badge-info"><%= f.getPolicy() %></span></td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } else { %>
                        <div class="empty-state">
                            <span class="icon">📭</span>
                            <h3>No files yet</h3>
                            <p>Upload your first encrypted file</p>
                            <a href="upload" class="btn btn-primary btn-sm mt-2">Upload File</a>
                        </div>
                    <% } %>
                </div>

                <!-- Recent Activity -->
                <div class="card">
                    <div class="card-header">
                        <h3>📋 Recent Activity</h3>
                    </div>
                    <% if (recentLogs != null && !recentLogs.isEmpty()) { %>
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr><th>Action</th><th>Time</th><th>Status</th></tr>
                                </thead>
                                <tbody>
                                    <% int logCount = 0; for (LogEntry l : recentLogs) { if (logCount++ >= 5) break; %>
                                    <tr>
                                        <td><%= l.getAction() %></td>
                                        <td style="font-size: 0.8rem;"><%= l.getTimestamp() %></td>
                                        <td>
                                            <% if (l.isAnomalyFlag()) { %>
                                                <span class="badge badge-danger">⚠️ Anomaly</span>
                                            <% } else { %>
                                                <span class="badge badge-success">✓ Normal</span>
                                            <% } %>
                                        </td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } else { %>
                        <div class="empty-state">
                            <span class="icon">📝</span>
                            <h3>No activity yet</h3>
                        </div>
                    <% } %>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="card">
                <div class="card-header">
                    <h3>⚡ Quick Actions</h3>
                </div>
                <div class="action-group">
                    <a href="upload" class="btn btn-primary">📤 Upload File</a>
                    <a href="files" class="btn btn-secondary">📁 View Files</a>
                    <a href="blockchain" class="btn btn-secondary">⛓️ Blockchain</a>
                    <a href="federated" class="btn btn-secondary">🤝 Federated Learning</a>
                    <a href="trust" class="btn btn-secondary">🛡️ Trust Scores</a>
                </div>
            </div>
        </main>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
