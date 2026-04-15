<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.iomt.model.*, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect("login"); return; }
    List<AccessRequest> pendingRequests = (List<AccessRequest>) request.getAttribute("pendingRequests");
    List<AccessRequest> myRequests = (List<AccessRequest>) request.getAttribute("myRequests");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Access Requests | IoMT Cloud</title>
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
                    <a href="access-request" class="nav-link active"><span class="icon">🔑</span> Access Requests</a>
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

        <main class="main-content">
            <div class="page-header">
                <h1>Access Requests 🔑</h1>
                <p>Manage file access requests</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">⚠️ <%= request.getAttribute("error") %></div>
            <% } %>
            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">✅ <%= request.getAttribute("success") %></div>
            <% } %>

            <!-- Pending Requests (for my files) -->
            <div class="card mb-3">
                <div class="card-header">
                    <h3>📥 Requests for My Files</h3>
                </div>
                <% if (pendingRequests != null && !pendingRequests.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr><th>Requester</th><th>Role</th><th>File</th><th>Requested</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                            <% for (AccessRequest ar : pendingRequests) { %>
                            <tr>
                                <td><strong><%= ar.getRequesterName() %></strong></td>
                                <td><span class="badge badge-primary"><%= ar.getRequesterRole() %></span></td>
                                <td><%= ar.getFileName() %></td>
                                <td style="font-size: 0.8rem;"><%= ar.getRequestedAt() %></td>
                                <td>
                                    <div class="action-group">
                                        <form action="access-request" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="approve">
                                            <input type="hidden" name="requestId" value="<%= ar.getId() %>">
                                            <button type="submit" class="btn btn-success btn-sm">✅ Approve</button>
                                        </form>
                                        <form action="access-request" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="deny">
                                            <input type="hidden" name="requestId" value="<%= ar.getId() %>">
                                            <button type="submit" class="btn btn-danger btn-sm">❌ Deny</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                    <div class="empty-state">
                        <span class="icon">📭</span>
                        <h3>No pending requests</h3>
                    </div>
                <% } %>
            </div>

            <!-- My Requests -->
            <div class="card">
                <div class="card-header">
                    <h3>📤 My Requests</h3>
                </div>
                <% if (myRequests != null && !myRequests.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr><th>File</th><th>Status</th><th>Requested</th><th>Resolved</th></tr>
                        </thead>
                        <tbody>
                            <% for (AccessRequest ar : myRequests) { %>
                            <tr>
                                <td><%= ar.getFileName() %></td>
                                <td>
                                    <span class="badge badge-<%= "approved".equals(ar.getStatus()) ? "success" : "pending".equals(ar.getStatus()) ? "warning" : "danger" %>">
                                        <%= ar.getStatus() %>
                                    </span>
                                </td>
                                <td style="font-size: 0.8rem;"><%= ar.getRequestedAt() %></td>
                                <td style="font-size: 0.8rem;"><%= ar.getResolvedAt() != null ? ar.getResolvedAt() : "—" %></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                    <div class="empty-state">
                        <span class="icon">📝</span>
                        <h3>You haven't made any requests</h3>
                        <p>Go to <a href="files">Files</a> to request access</p>
                    </div>
                <% } %>
            </div>
        </main>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
