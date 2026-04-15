<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.iomt.model.*, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect("login"); return; }
    List<Block> blocks = (List<Block>) request.getAttribute("blocks");
    Boolean chainValid = (Boolean) request.getAttribute("chainValid");
    Integer blockCount = (Integer) request.getAttribute("blockCount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Blockchain | IoMT Cloud</title>
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
                    <a href="blockchain" class="nav-link active"><span class="icon">⛓️</span> Blockchain</a>
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
                <h1>Blockchain Viewer ⛓️</h1>
                <p>Immutable audit trail for all system events</p>
            </div>

            <!-- Chain Status -->
            <div class="stats-grid">
                <div class="stat-card emerald">
                    <div class="stat-icon">⛓️</div>
                    <div class="stat-info">
                        <h3><%= blockCount != null ? blockCount : 0 %></h3>
                        <p>Total Blocks</p>
                    </div>
                </div>
                <div class="stat-card <%= chainValid != null && chainValid ? "emerald" : "rose" %>">
                    <div class="stat-icon"><%= chainValid != null && chainValid ? "✅" : "❌" %></div>
                    <div class="stat-info">
                        <h3><%= chainValid != null && chainValid ? "Valid" : "Invalid" %></h3>
                        <p>Chain Integrity</p>
                    </div>
                </div>
            </div>

            <!-- Chain Validation Badge -->
            <div class="mb-3">
                <span class="chain-valid <%= chainValid != null && chainValid ? "valid" : "invalid" %>">
                    <%= chainValid != null && chainValid ? "✅ Blockchain integrity verified - No tampering detected" : "❌ Blockchain integrity compromised!" %>
                </span>
            </div>

            <!-- Blockchain Visual -->
            <% if (blocks != null && !blocks.isEmpty()) { %>
                <div class="blockchain-chain">
                    <% for (int i = 0; i < blocks.size(); i++) {
                        Block b = blocks.get(i);
                    %>
                        <div class="block-item" style="animation: fadeInUp 0.3s ease forwards; animation-delay: <%= i * 0.1 %>s;">
                            <div class="block-header">
                                <span class="block-index">Block #<%= b.getBlockIndex() %></span>
                                <span class="block-timestamp">🕐 <%= b.getFormattedTimestamp() %></span>
                            </div>

                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 8px;">
                                <div>
                                    <span style="font-size: 0.7rem; color: var(--text-muted); text-transform: uppercase;">Previous Hash</span>
                                    <div class="block-hash" style="font-size: 0.7rem;"><%= b.getPrevHash() %></div>
                                </div>
                                <div>
                                    <span style="font-size: 0.7rem; color: var(--text-muted); text-transform: uppercase;">Current Hash</span>
                                    <div class="block-hash" style="font-size: 0.7rem; color: var(--accent-emerald);"><%= b.getHash() %></div>
                                </div>
                            </div>

                            <div>
                                <span style="font-size: 0.7rem; color: var(--text-muted); text-transform: uppercase;">Data</span>
                                <div class="block-data"><%= b.getData() %></div>
                            </div>
                        </div>

                        <% if (i < blocks.size() - 1) { %>
                            <div class="block-connector">🔗</div>
                        <% } %>
                    <% } %>
                </div>
            <% } else { %>
                <div class="card">
                    <div class="empty-state">
                        <span class="icon">⛓️</span>
                        <h3>No blocks yet</h3>
                        <p>Blockchain events will appear when files are uploaded or accessed</p>
                    </div>
                </div>
            <% } %>
        </main>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
