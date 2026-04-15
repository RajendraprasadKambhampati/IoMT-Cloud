<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.iomt.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect("login"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload File | IoMT Cloud</title>
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
                    <a href="<%= "admin".equals(user.getRole()) ? "admin-dashboard" : "dashboard" %>" class="nav-link"><span class="icon">📊</span> Dashboard</a>
                    <a href="upload" class="nav-link active"><span class="icon">📤</span> Upload File</a>
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
                <h1>Upload Encrypted File 📤</h1>
                <p>Files are encrypted with AES and protected by Attribute-Based Encryption policy</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">⚠️ <%= request.getAttribute("error") %></div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    ✅ <%= request.getAttribute("success") %>
                    <% if (request.getAttribute("fileHash") != null) { %>
                        <br><small>Hash: <code><%= request.getAttribute("fileHash") %></code></small>
                    <% } %>
                    <% if (request.getAttribute("policy") != null) { %>
                        <br><small>Policy: <code><%= request.getAttribute("policy") %></code></small>
                    <% } %>
                </div>
            <% } %>

            <form action="upload" method="post" enctype="multipart/form-data">
                <div class="card mb-3">
                    <div class="card-title">📄 Select File</div>

                    <!-- Upload Area -->
                    <div class="upload-area" id="uploadArea">
                        <span class="upload-icon">☁️</span>
                        <p><strong>Click to browse</strong> or drag & drop your file here</p>
                        <p style="font-size: 0.8rem; margin-top: 8px;">Max file size: 50MB</p>
                    </div>
                    <input type="file" id="fileInput" name="file" style="display: none;" required>
                </div>

                <!-- ABE Policy Builder -->
                <div class="card mb-3">
                    <div class="card-title">🔐 Access Policy (ABE)</div>
                    <p style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 16px;">
                        Define who can decrypt and access this file based on their attributes
                    </p>

                    <div class="policy-builder">
                        <h4>Policy Conditions</h4>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="policyRole">Required Role</label>
                                <select id="policyRole" name="policyRole" class="form-control">
                                    <option value="any">Any Role</option>
                                    <option value="doctor">Doctor</option>
                                    <option value="hospital">Hospital</option>
                                    <option value="device">Device</option>
                                    <option value="admin">Admin</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="policyDept">Required Department</label>
                                <select id="policyDept" name="policyDept" class="form-control">
                                    <option value="any">Any Department</option>
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
                        </div>
                        <div class="form-group">
                            <label for="policyLevel">Minimum Clearance Level</label>
                            <select id="policyLevel" name="policyLevel" class="form-control">
                                <option value="0">No minimum</option>
                                <option value="1">Level 1+</option>
                                <option value="2">Level 2+</option>
                                <option value="3">Level 3+</option>
                                <option value="4">Level 4+</option>
                                <option value="5">Level 5 only</option>
                            </select>
                        </div>

                        <div class="policy-preview">
                            <strong>Policy:</strong> <span id="policyPreview">No restrictions</span>
                        </div>
                    </div>
                </div>

                <!-- Encryption Info -->
                <div class="card mb-3">
                    <div class="card-title">🔒 Encryption Details</div>
                    <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 16px;">
                        <div>
                            <p style="font-size: 0.8rem; color: var(--text-muted);">Algorithm</p>
                            <p style="font-weight: 600;">AES-128-CBC</p>
                        </div>
                        <div>
                            <p style="font-size: 0.8rem; color: var(--text-muted);">Key Protection</p>
                            <p style="font-weight: 600;">CP-ABE Policy</p>
                        </div>
                        <div>
                            <p style="font-size: 0.8rem; color: var(--text-muted);">Integrity</p>
                            <p style="font-weight: 600;">SHA-256 Hash</p>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-lg">
                    🔐 Encrypt & Upload
                </button>
            </form>
        </main>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
