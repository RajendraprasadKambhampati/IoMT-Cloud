package com.iomt.dao;

import com.iomt.model.LogEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LogDAO - Data Access Object for system logs
 * Manages activity logs and anomaly-flagged entries
 */
public class LogDAO {

    /**
     * Add a new log entry
     */
    public boolean addLog(LogEntry log) {
        String sql = "INSERT INTO logs (user_id, action, details, ip_address, anomaly_flag) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (log.getUserId() > 0) {
                ps.setInt(1, log.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, log.getAction());
            ps.setString(3, log.getDetails());
            ps.setString(4, log.getIpAddress());
            ps.setBoolean(5, log.isAnomalyFlag());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[LogDAO] addLog error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get logs by user ID
     */
    public List<LogEntry> getLogsByUser(int userId) {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT l.*, u.name as user_name FROM logs l LEFT JOIN users u ON l.user_id = u.id WHERE l.user_id = ? ORDER BY l.timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logs.add(extractLog(rs));
            }
        } catch (Exception e) {
            System.err.println("[LogDAO] getLogsByUser error: " + e.getMessage());
        }
        return logs;
    }

    /**
     * Get all anomaly-flagged logs
     */
    public List<LogEntry> getAnomalyLogs() {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT l.*, u.name as user_name FROM logs l LEFT JOIN users u ON l.user_id = u.id WHERE l.anomaly_flag = TRUE ORDER BY l.timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                logs.add(extractLog(rs));
            }
        } catch (Exception e) {
            System.err.println("[LogDAO] getAnomalyLogs error: " + e.getMessage());
        }
        return logs;
    }

    /**
     * Get all logs (admin view)
     */
    public List<LogEntry> getAllLogs() {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT l.*, u.name as user_name FROM logs l LEFT JOIN users u ON l.user_id = u.id ORDER BY l.timestamp DESC LIMIT 500";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                logs.add(extractLog(rs));
            }
        } catch (Exception e) {
            System.err.println("[LogDAO] getAllLogs error: " + e.getMessage());
        }
        return logs;
    }

    /**
     * Get recent logs
     */
    public List<LogEntry> getRecentLogs(int limit) {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT l.*, u.name as user_name FROM logs l LEFT JOIN users u ON l.user_id = u.id ORDER BY l.timestamp DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logs.add(extractLog(rs));
            }
        } catch (Exception e) {
            System.err.println("[LogDAO] getRecentLogs error: " + e.getMessage());
        }
        return logs;
    }

    /**
     * Get anomaly count
     */
    public int getAnomalyCount() {
        String sql = "SELECT COUNT(*) FROM logs WHERE anomaly_flag = TRUE";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[LogDAO] getAnomalyCount error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Count failed logins for a user in a time window
     */
    public int getFailedLoginCount(int userId, long sinceTimestamp) {
        String sql = "SELECT COUNT(*) FROM logs WHERE user_id = ? AND action = 'LOGIN_FAILED' AND timestamp >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, new Timestamp(sinceTimestamp));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[LogDAO] getFailedLoginCount error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Count file accesses for a user in a time window
     */
    public int getFileAccessCount(int userId, long sinceTimestamp) {
        String sql = "SELECT COUNT(*) FROM logs WHERE user_id = ? AND (action = 'FILE_DOWNLOAD' OR action = 'FILE_VIEW') AND timestamp >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, new Timestamp(sinceTimestamp));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[LogDAO] getFileAccessCount error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total log count
     */
    public int getTotalLogCount() {
        String sql = "SELECT COUNT(*) FROM logs";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[LogDAO] getTotalLogCount error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Extract LogEntry from ResultSet
     */
    private LogEntry extractLog(ResultSet rs) throws SQLException {
        LogEntry log = new LogEntry();
        log.setId(rs.getInt("id"));
        int userId = rs.getInt("user_id");
        log.setUserId(rs.wasNull() ? 0 : userId);
        log.setAction(rs.getString("action"));
        log.setDetails(rs.getString("details"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setTimestamp(rs.getTimestamp("timestamp"));
        log.setAnomalyFlag(rs.getBoolean("anomaly_flag"));
        try { log.setUserName(rs.getString("user_name")); } catch (SQLException ignored) {}
        return log;
    }
}
