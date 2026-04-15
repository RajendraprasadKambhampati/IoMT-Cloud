package com.iomt.trust;

import com.iomt.dao.LogDAO;
import com.iomt.dao.UserDAO;
import com.iomt.model.LogEntry;
import com.iomt.model.User;

import java.util.List;

/**
 * TrustManager - Trust Score Management System
 * Manages dynamic trust scores based on user behavior:
 * - Successful operations increase trust (+1)
 * - Failed access attempts decrease trust (-5)
 * - Anomalies decrease trust severely (-10)
 * - Access is restricted when trust falls below threshold
 *
 * Trust Levels:
 * 80-100: HIGH (full access)
 * 50-79:  MEDIUM (standard access)
 * 30-49:  LOW (restricted access)
 * 0-29:   BLOCKED (no access)
 */
public class TrustManager {

    private UserDAO userDAO;
    private LogDAO logDAO;

    // Trust score modifiers
    private static final double REWARD_SUCCESS = 1.0;
    private static final double PENALTY_FAILED_ACCESS = -5.0;
    private static final double PENALTY_ANOMALY = -10.0;
    private static final double PENALTY_UNAUTHORIZED = -15.0;
    private static final double REWARD_TIME_RECOVERY = 0.5;  // Per successful action after penalty

    // Trust thresholds
    public static final double THRESHOLD_HIGH = 80.0;
    public static final double THRESHOLD_MEDIUM = 50.0;
    public static final double THRESHOLD_LOW = 30.0;

    public TrustManager() {
        this.userDAO = new UserDAO();
        this.logDAO = new LogDAO();
    }

    public TrustManager(UserDAO userDAO, LogDAO logDAO) {
        this.userDAO = userDAO;
        this.logDAO = logDAO;
    }

    /**
     * Update trust score based on action outcome
     *
     * @param userId User whose trust to update
     * @param action The action performed
     * @param success Whether the action was successful
     * @param isAnomaly Whether the action was flagged as anomaly
     * @return New trust score
     */
    public double updateTrust(int userId, String action, boolean success, boolean isAnomaly) {
        User user = userDAO.getUserById(userId);
        if (user == null) return 0;

        double currentScore = user.getTrustScore();
        double modifier = 0;

        if (isAnomaly) {
            modifier = PENALTY_ANOMALY;
        } else if (!success) {
            if ("ACCESS_DENIED".equals(action) || "UNAUTHORIZED".equals(action)) {
                modifier = PENALTY_UNAUTHORIZED;
            } else {
                modifier = PENALTY_FAILED_ACCESS;
            }
        } else {
            modifier = REWARD_SUCCESS;
        }

        double newScore = Math.max(0, Math.min(100, currentScore + modifier));
        userDAO.updateTrustScore(userId, newScore);

        System.out.printf("[TrustManager] User %d: %.1f -> %.1f (%s%s)%n",
                userId, currentScore, newScore,
                modifier >= 0 ? "+" : "", modifier);

        return newScore;
    }

    /**
     * Check if user has sufficient trust for access
     *
     * @param user The user requesting access
     * @return true if trust allows access
     */
    public static boolean checkAccessByTrust(User user) {
        if (user == null) return false;
        if ("admin".equals(user.getRole())) return true; // Admin always has access
        return user.getTrustScore() >= THRESHOLD_LOW;
    }

    /**
     * Get trust level label for a trust score
     */
    public static String getTrustLevel(double score) {
        if (score >= THRESHOLD_HIGH) return "HIGH";
        if (score >= THRESHOLD_MEDIUM) return "MEDIUM";
        if (score >= THRESHOLD_LOW) return "LOW";
        return "BLOCKED";
    }

    /**
     * Get CSS class for trust level (for UI display)
     */
    public static String getTrustLevelClass(double score) {
        if (score >= THRESHOLD_HIGH) return "trust-high";
        if (score >= THRESHOLD_MEDIUM) return "trust-medium";
        if (score >= THRESHOLD_LOW) return "trust-low";
        return "trust-blocked";
    }

    /**
     * Recalculate trust score based on complete log history
     * Used for periodic trust recalculation
     */
    public double recalculateTrust(int userId) {
        User user = userDAO.getUserById(userId);
        if (user == null) return 0;

        List<LogEntry> logs = logDAO.getLogsByUser(userId);
        double score = 100.0; // Start from initial score

        for (LogEntry log : logs) {
            if (log.isAnomalyFlag()) {
                score += PENALTY_ANOMALY;
            } else if (log.getAction() != null) {
                switch (log.getAction()) {
                    case "LOGIN_FAILED":
                    case "ACCESS_DENIED":
                        score += PENALTY_FAILED_ACCESS;
                        break;
                    case "LOGIN_SUCCESS":
                    case "FILE_UPLOAD":
                    case "FILE_DOWNLOAD":
                        score += REWARD_SUCCESS;
                        break;
                    default:
                        break;
                }
            }
        }

        // Clamp between 0 and 100
        score = Math.max(0, Math.min(100, score));
        userDAO.updateTrustScore(userId, score);

        return score;
    }

    /**
     * Get trust score for a user
     */
    public double getTrustScore(int userId) {
        User user = userDAO.getUserById(userId);
        return user != null ? user.getTrustScore() : 0;
    }
}
