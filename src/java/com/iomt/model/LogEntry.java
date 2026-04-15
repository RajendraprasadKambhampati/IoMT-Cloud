package com.iomt.model;

import java.sql.Timestamp;

/**
 * LogEntry Model - Represents a system activity log entry
 * Maps to the 'logs' table in the database
 */
public class LogEntry {
    private int id;
    private int userId;
    private String action;         // e.g., "LOGIN", "FILE_UPLOAD", "ACCESS_DENIED"
    private String details;        // Additional info about the action
    private String ipAddress;
    private Timestamp timestamp;
    private boolean anomalyFlag;   // True if this action was flagged as anomalous

    // Additional display field
    private String userName;

    // Default constructor
    public LogEntry() {
        this.anomalyFlag = false;
    }

    // Constructor for creating a new log
    public LogEntry(int userId, String action, String details, String ipAddress) {
        this();
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public boolean isAnomalyFlag() { return anomalyFlag; }
    public void setAnomalyFlag(boolean anomalyFlag) { this.anomalyFlag = anomalyFlag; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    /**
     * Get severity level based on action type
     */
    public String getSeverity() {
        if (action == null) return "INFO";
        if (action.contains("DENIED") || action.contains("UNAUTHORIZED") || action.contains("BLOCKED")) {
            return "HIGH";
        }
        if (action.contains("FAILED") || action.contains("ANOMALY")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    @Override
    public String toString() {
        return "LogEntry{id=" + id + ", action='" + action + "', anomaly=" + anomalyFlag + "}";
    }
}
