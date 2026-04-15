package com.iomt.dao;

import com.iomt.model.AccessRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AccessRequestDAO - Data Access Object for file access requests
 * Manages request creation, approval, and denial
 */
public class AccessRequestDAO {

    /**
     * Create a new access request
     */
    public boolean createRequest(AccessRequest request) {
        // Check if request already exists
        String checkSql = "SELECT COUNT(*) FROM access_requests WHERE requester_id = ? AND file_id = ? AND status = 'pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, request.getRequesterId());
            checkPs.setInt(2, request.getFileId());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Request already exists
            }
        } catch (Exception e) {
            System.err.println("[AccessRequestDAO] check existing error: " + e.getMessage());
        }

        String sql = "INSERT INTO access_requests (requester_id, file_id, status) VALUES (?, ?, 'pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getRequesterId());
            ps.setInt(2, request.getFileId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AccessRequestDAO] createRequest error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get pending requests for files owned by a user
     */
    public List<AccessRequest> getPendingRequestsForOwner(int ownerId) {
        List<AccessRequest> requests = new ArrayList<>();
        String sql = "SELECT ar.*, u.name as requester_name, u.role as requester_role, f.original_filename as file_name " +
                     "FROM access_requests ar " +
                     "JOIN users u ON ar.requester_id = u.id " +
                     "JOIN files f ON ar.file_id = f.id " +
                     "WHERE f.owner_id = ? AND ar.status = 'pending' " +
                     "ORDER BY ar.requested_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                requests.add(extractRequest(rs));
            }
        } catch (Exception e) {
            System.err.println("[AccessRequestDAO] getPendingRequestsForOwner error: " + e.getMessage());
        }
        return requests;
    }

    /**
     * Get requests made by a user
     */
    public List<AccessRequest> getRequestsByUser(int userId) {
        List<AccessRequest> requests = new ArrayList<>();
        String sql = "SELECT ar.*, u.name as requester_name, u.role as requester_role, f.original_filename as file_name " +
                     "FROM access_requests ar " +
                     "JOIN users u ON ar.requester_id = u.id " +
                     "JOIN files f ON ar.file_id = f.id " +
                     "WHERE ar.requester_id = ? ORDER BY ar.requested_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                requests.add(extractRequest(rs));
            }
        } catch (Exception e) {
            System.err.println("[AccessRequestDAO] getRequestsByUser error: " + e.getMessage());
        }
        return requests;
    }

    /**
     * Update request status (approve/deny)
     */
    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE access_requests SET status = ?, resolved_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[AccessRequestDAO] updateRequestStatus error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if user has approved access to a file
     */
    public boolean hasApprovedAccess(int userId, int fileId) {
        String sql = "SELECT COUNT(*) FROM access_requests WHERE requester_id = ? AND file_id = ? AND status = 'approved'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, fileId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {
            System.err.println("[AccessRequestDAO] hasApprovedAccess error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get all pending requests count
     */
    public int getPendingCount(int ownerId) {
        String sql = "SELECT COUNT(*) FROM access_requests ar JOIN files f ON ar.file_id = f.id WHERE f.owner_id = ? AND ar.status = 'pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[AccessRequestDAO] getPendingCount error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Extract AccessRequest from ResultSet
     */
    private AccessRequest extractRequest(ResultSet rs) throws SQLException {
        AccessRequest req = new AccessRequest();
        req.setId(rs.getInt("id"));
        req.setRequesterId(rs.getInt("requester_id"));
        req.setFileId(rs.getInt("file_id"));
        req.setStatus(rs.getString("status"));
        req.setRequestedAt(rs.getTimestamp("requested_at"));
        req.setResolvedAt(rs.getTimestamp("resolved_at"));
        try { req.setRequesterName(rs.getString("requester_name")); } catch (SQLException ignored) {}
        try { req.setFileName(rs.getString("file_name")); } catch (SQLException ignored) {}
        try { req.setRequesterRole(rs.getString("requester_role")); } catch (SQLException ignored) {}
        return req;
    }
}
