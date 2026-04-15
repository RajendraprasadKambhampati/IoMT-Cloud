<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.iomt.model.*, com.iomt.federated.FederatedLearning, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect("login"); return; }
    Integer latestRound = (Integer) request.getAttribute("latestRound");
    List<FederatedUpdate> allUpdates = (List<FederatedUpdate>) request.getAttribute("allUpdates");
    List<FederatedUpdate> latestUpdates = (List<FederatedUpdate>) request.getAttribute("latestUpdates");
    String[] deviceIds = (String[]) request.getAttribute("deviceIds");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Federated Learning | IoMT Cloud</title>
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
                    <a href="trust" class="nav-link"><span class="icon">🛡️</span> Trust Scores</a>
                </div>
                <div class="nav-section">
                    <div class="nav-section-title">AI / ML</div>
                    <a href="federated" class="nav-link active"><span class="icon">🤝</span> Federated Learning</a>
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
                    <h1>Federated Learning 🤝</h1>
                    <p>Distributed model training across IoMT devices — no raw data sharing</p>
                </div>
                <form action="federated" method="post">
                    <input type="hidden" name="action" value="runRound">
                    <button type="submit" class="btn btn-primary">▶️ Run Training Round</button>
                </form>
            </div>

            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">✅ <%= request.getAttribute("success") %></div>
            <% } %>

            <!-- Stats -->
            <div class="stats-grid">
                <div class="stat-card violet">
                    <div class="stat-icon">🔄</div>
                    <div class="stat-info">
                        <h3><%= latestRound != null ? latestRound : 0 %></h3>
                        <p>Rounds Completed</p>
                    </div>
                </div>
                <div class="stat-card cyan">
                    <div class="stat-icon">📟</div>
                    <div class="stat-info">
                        <h3><%= deviceIds != null ? deviceIds.length : 5 %></h3>
                        <p>Simulated Devices</p>
                    </div>
                </div>
                <div class="stat-card emerald">
                    <div class="stat-icon">🧠</div>
                    <div class="stat-info">
                        <h3>FedAvg</h3>
                        <p>Aggregation Method</p>
                    </div>
                </div>
            </div>

            <!-- How It Works -->
            <div class="card mb-3">
                <div class="card-header">
                    <h3>📖 How Federated Learning Works</h3>
                </div>
                <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; text-align: center;">
                    <div style="padding: 16px; background: var(--bg-glass); border-radius: var(--radius-sm);">
                        <span style="font-size: 2rem;">📟</span>
                        <p style="font-weight: 600; margin-top: 8px;">1. Local Training</p>
                        <p style="font-size: 0.75rem; color: var(--text-muted);">Each device trains model locally</p>
                    </div>
                    <div style="padding: 16px; background: var(--bg-glass); border-radius: var(--radius-sm);">
                        <span style="font-size: 2rem;">📤</span>
                        <p style="font-weight: 600; margin-top: 8px;">2. Share Weights</p>
                        <p style="font-size: 0.75rem; color: var(--text-muted);">Only weights sent, not data</p>
                    </div>
                    <div style="padding: 16px; background: var(--bg-glass); border-radius: var(--radius-sm);">
                        <span style="font-size: 2rem;">🔄</span>
                        <p style="font-weight: 600; margin-top: 8px;">3. Aggregate</p>
                        <p style="font-size: 0.75rem; color: var(--text-muted);">Server averages all weights</p>
                    </div>
                    <div style="padding: 16px; background: var(--bg-glass); border-radius: var(--radius-sm);">
                        <span style="font-size: 2rem;">🌐</span>
                        <p style="font-weight: 600; margin-top: 8px;">4. Global Model</p>
                        <p style="font-size: 0.75rem; color: var(--text-muted);">Updated model distributed</p>
                    </div>
                </div>
            </div>

            <!-- Latest Round Results -->
            <% if (latestUpdates != null && !latestUpdates.isEmpty()) { %>
            <div class="card mb-3">
                <div class="card-header">
                    <h3>📊 Round <%= latestRound %> Results</h3>
                </div>

                <!-- Global Weights -->
                <div style="margin-bottom: 20px;">
                    <h4 style="color: var(--accent-emerald); margin-bottom: 8px;">🌐 Global Model Weights (Aggregated)</h4>
                    <div class="fl-weights"><%= latestUpdates.get(0).getGlobalWeights() %></div>
                </div>

                <!-- Device Cards -->
                <h4 style="margin-bottom: 12px;">📟 Device Local Updates</h4>
                <div class="fl-device-grid">
                    <% for (FederatedUpdate fu : latestUpdates) {
                        double divergence = FederatedLearning.calculateDivergence(fu.getLocalWeights(), fu.getGlobalWeights());
                    %>
                    <div class="fl-device-card">
                        <h4>📟 <%= fu.getDeviceId() %></h4>
                        <p style="font-size: 0.75rem; color: var(--text-muted);">Local Weights:</p>
                        <div class="fl-weights"><%= fu.getLocalWeights() %></div>
                        <div class="fl-accuracy">
                            Accuracy: <span style="color: var(--accent-emerald);"><%= fu.getFormattedAccuracy() %></span>
                        </div>
                        <div style="margin-top: 6px; font-size: 0.8rem;">
                            Divergence: <span style="color: var(--accent-amber);"><%= String.format("%.4f", divergence) %></span>
                        </div>
                    </div>
                    <% } %>
                </div>
            </div>
            <% } %>

            <!-- All Rounds History -->
            <div class="card">
                <div class="card-header">
                    <h3>📜 Training History</h3>
                </div>
                <% if (allUpdates != null && !allUpdates.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr><th>Round</th><th>Device</th><th>Accuracy</th><th>Local Weights</th><th>Global Weights</th></tr>
                        </thead>
                        <tbody>
                            <% int shown = 0; for (FederatedUpdate fu : allUpdates) { if (shown++ >= 25) break; %>
                            <tr>
                                <td><span class="badge badge-violet">Round <%= fu.getRoundNumber() %></span></td>
                                <td><%= fu.getDeviceId() %></td>
                                <td><span style="color: var(--accent-emerald); font-weight: 600;"><%= fu.getFormattedAccuracy() %></span></td>
                                <td style="max-width: 200px; overflow: hidden; text-overflow: ellipsis;">
                                    <span class="mono" style="font-size: 0.7rem;"><%= fu.getLocalWeights() %></span>
                                </td>
                                <td style="max-width: 200px; overflow: hidden; text-overflow: ellipsis;">
                                    <span class="mono" style="font-size: 0.7rem;"><%= fu.getGlobalWeights() != null ? fu.getGlobalWeights() : "—" %></span>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                    <div class="empty-state">
                        <span class="icon">🤝</span>
                        <h3>No training rounds yet</h3>
                        <p>Click "Run Training Round" to simulate federated learning</p>
                    </div>
                <% } %>
            </div>
        </main>
    </div>

    <script src="js/main.js"></script>
</body>
</html>
