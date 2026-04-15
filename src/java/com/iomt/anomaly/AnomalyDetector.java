package com.iomt.anomaly;

import com.iomt.dao.LogDAO;
import com.iomt.dao.UserDAO;
import com.iomt.model.LogEntry;
import com.iomt.model.User;

/**
 * AnomalyDetector - Rule-based anomaly detection system
 * Detects suspicious behavior patterns:
 * 1. Multiple failed logins (brute force attempt)
 * 2. Excessive file access (data exfiltration attempt)
 * 3. Unauthorized access attempts
 * 4. Access from restricted/blocked accounts
 */
public class AnomalyDetector {

    private LogDAO logDAO;
    private UserDAO userDAO;

    // Thresholds
    private static final int MAX_FAILED_LOGINS = 3;           // Max failures in time window
    private static final long FAILED_LOGIN_WINDOW = 600000;   // 10 minutes in ms
    private static final int MAX_FILE_ACCESS = 10;             // Max file accesses in time window
    private static final long FILE_ACCESS_WINDOW = 300000;     // 5 minutes in ms

    public AnomalyDetector() {
        this.logDAO = new LogDAO();
        this.userDAO = new UserDAO();
    }

    public AnomalyDetector(LogDAO logDAO, UserDAO userDAO) {
        this.logDAO = logDAO;
        this.userDAO = userDAO;
    }

    /**
     * Check for failed login anomaly
     * Flags if user has more than MAX_FAILED_LOGINS in the recent time window
     *
     * @param userId User ID to check
     * @return Anomaly description or null if normal
     */
    public String checkFailedLogins(int userId) {
        long since = System.currentTimeMillis() - FAILED_LOGIN_WINDOW;
        int failedCount = logDAO.getFailedLoginCount(userId, since);

        if (failedCount >= MAX_FAILED_LOGINS) {
            return String.format("BRUTE_FORCE: %d failed login attempts in last 10 minutes", failedCount);
        }
        return null;
    }

    /**
     * Check for excessive file access anomaly
     * Flags if user accesses more than MAX_FILE_ACCESS files in the time window
     *
     * @param userId User ID to check
     * @return Anomaly description or null if normal
     */
    public String checkAccessPattern(int userId) {
        long since = System.currentTimeMillis() - FILE_ACCESS_WINDOW;
        int accessCount = logDAO.getFileAccessCount(userId, since);

        if (accessCount >= MAX_FILE_ACCESS) {
            return String.format("EXCESSIVE_ACCESS: %d file accesses in last 5 minutes", accessCount);
        }
        return null;
    }

    /**
     * Check for unauthorized access attempt
     * Flags when a restricted/blocked user tries to perform actions
     *
     * @param userId User ID to check
     * @return Anomaly description or null if normal
     */
    public String checkUnauthorizedAccess(int userId) {
        User user = userDAO.getUserById(userId);
        if (user == null) return "UNKNOWN_USER: User ID not found";

        if ("blocked".equals(user.getStatus())) {
            return "BLOCKED_USER_ACCESS: Blocked user attempting access";
        }
        if ("restricted".equals(user.getStatus())) {
            return "RESTRICTED_USER_ACCESS: Restricted user attempting sensitive operation";
        }
        if (user.getTrustScore() < 30) {
            return "LOW_TRUST_ACCESS: User with critically low trust score attempting access";
        }
        return null;
    }

    /**
     * Run all anomaly checks for a user action and log/flag accordingly
     *
     * @param userId User ID performing the action
     * @param action The action being performed
     * @param details Additional details
     * @param ipAddress IP address of the request
     * @return AnomalyResult containing detection results
     */
    public AnomalyResult analyzeAndFlag(int userId, String action, String details, String ipAddress) {
        AnomalyResult result = new AnomalyResult();

        // Check for failed login pattern
        if ("LOGIN_FAILED".equals(action)) {
            String loginAnomaly = checkFailedLogins(userId);
            if (loginAnomaly != null) {
                result.setAnomaly(true);
                result.addDetail(loginAnomaly);
                result.setSeverity("HIGH");
            }
        }

        // Check for excessive file access
        if ("FILE_DOWNLOAD".equals(action) || "FILE_VIEW".equals(action)) {
            String accessAnomaly = checkAccessPattern(userId);
            if (accessAnomaly != null) {
                result.setAnomaly(true);
                result.addDetail(accessAnomaly);
                result.setSeverity("MEDIUM");
            }
        }

        // Check for unauthorized access
        String unauthAnomaly = checkUnauthorizedAccess(userId);
        if (unauthAnomaly != null) {
            result.setAnomaly(true);
            result.addDetail(unauthAnomaly);
            result.setSeverity("HIGH");
        }

        // Check for access denied events
        if ("ACCESS_DENIED".equals(action)) {
            result.setAnomaly(true);
            result.addDetail("POLICY_VIOLATION: User attempted to access unauthorized resource");
            if (result.getSeverity() == null) result.setSeverity("MEDIUM");
        }

        // Log the action with anomaly flag
        LogEntry logEntry = new LogEntry(userId, action, details, ipAddress);
        logEntry.setAnomalyFlag(result.isAnomaly());
        if (result.isAnomaly()) {
            logEntry.setDetails(details + " [ANOMALY: " + result.getDetails() + "]");
        }
        logDAO.addLog(logEntry);

        return result;
    }

    /**
     * Inner class to hold anomaly detection results
     */
    public static class AnomalyResult {
        private boolean isAnomaly;
        private String severity;    // HIGH, MEDIUM, LOW
        private StringBuilder details;

        public AnomalyResult() {
            this.isAnomaly = false;
            this.details = new StringBuilder();
        }

        public boolean isAnomaly() { return isAnomaly; }
        public void setAnomaly(boolean anomaly) { isAnomaly = anomaly; }

        public String getSeverity() { return severity; }
        public void setSeverity(String severity) {
            // Only upgrade severity, never downgrade
            if (this.severity == null || "HIGH".equals(severity)) {
                this.severity = severity;
            }
        }

        public String getDetails() { return details.toString(); }
        public void addDetail(String detail) {
            if (details.length() > 0) details.append("; ");
            details.append(detail);
        }
    }
}
