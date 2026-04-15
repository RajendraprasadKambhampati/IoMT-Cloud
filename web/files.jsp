<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.iomt.model.*, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect("login"); return; }
    List<FileRecord> myFiles = (List<FileRecord>) request.getAttribute("myFiles");
    List<FileRecord> allFiles = (List<FileRecord>) request.getAttribute("allFiles");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Files | IoMT Cloud</title>
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
                    <a href="files" class="nav-link active"><span class="icon">📁</span> My Files</a>
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

        <main class="main-content">
            <div class="page-header">
                <h1>File Manager 📁</h1>
                <p>View, download, and manage encrypted files</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">⚠️ <%= request.getAttribute("error") %>
                    <% if (request.getAttribute("filePolicy") != null) { %>
                        <br><small>File Policy: <code><%= request.getAttribute("filePolicy") %></code></small>
                        <br><small>Your Attributes: <code><%= request.getAttribute("userAttributes") %></code></small>
                    <% } %>
                </div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">✅ <%= request.getAttribute("success") %></div>
            <% } %>

            <!-- My Files -->
            <div class="card mb-3">
                <div class="card-header">
                    <h3>📄 My Files</h3>
                    <a href="upload" class="btn btn-primary btn-sm">📤 Upload New</a>
                </div>
                <% if (myFiles != null && !myFiles.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr><th>File Name</th><th>Size</th><th>Policy</th><th>Hash</th><th>Uploaded</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                            <% for (FileRecord f : myFiles) { %>
                            <tr>
                                <td><strong>📄 <%= f.getOriginalFilename() %></strong></td>
                                <td><%= f.getFormattedSize() %></td>
                                <td><span class="badge badge-info"><%= f.getPolicy() %></span></td>
                                <td><span class="hash-display" title="<%= f.getFileHash() %>"><%= f.getFileHash().substring(0, 16) %>...</span></td>
                                <td style="font-size: 0.8rem;"><%= f.getCreatedAt() %></td>
                                <td>
                                    <a href="download?id=<%= f.getId() %>" class="btn btn-success btn-sm">⬇️ Download</a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                    <div class="empty-state">
                        <span class="icon">📭</span>
                        <h3>No files uploaded yet</h3>
                        <a href="upload" class="btn btn-primary btn-sm mt-2">Upload First File</a>
                    </div>
                <% } %>
            </div>

            <!-- All Files / Accessible Files -->
            <div class="card">
                <div class="card-header">
                    <h3>🌐 All Files in Cloud</h3>
                </div>
                <% if (allFiles != null && !allFiles.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr><th>File Name</th><th>Owner</th><th>Size</th><th>Policy</th><th>Hash</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                            <% for (FileRecord f : allFiles) { %>
                            <tr>
                                <td><strong>📄 <%= f.getOriginalFilename() %></strong></td>
                                <td><%= f.getOwnerName() != null ? f.getOwnerName() : "Unknown" %></td>
                                <td><%= f.getFormattedSize() %></td>
                                <td><span class="badge badge-info"><%= f.getPolicy() %></span></td>
                                <td><span class="hash-display" title="<%= f.getFileHash() %>"><%= f.getFileHash().substring(0, 16) %>...</span></td>
                                <td>
                                    <div class="action-group">
                                        <a href="download?id=<%= f.getId() %>" class="btn btn-success btn-sm">⬇️ Download</a>
                                        <% if (f.getOwnerId() != user.getId()) { %>
                                        <form action="access-request" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="request">
                                            <input type="hidden" name="fileId" value="<%= f.getId() %>">
                                            <button type="submit" class="btn btn-warning btn-sm">🔑 Request Access</button>
                                        </form>
                                        <% } %>
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
                        <h3>No files in cloud storage</h3>
                    </div>
                <% } %>
            </div>
        </main>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
