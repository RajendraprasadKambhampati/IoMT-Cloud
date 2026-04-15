package com.iomt.dao;

import com.iomt.model.FederatedUpdate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FederatedDAO - Data Access Object for Federated Learning updates
 * Stores local model weights and aggregated global weights
 */
public class FederatedDAO {

    /**
     * Save a federated learning update
     */
    public boolean saveUpdate(FederatedUpdate update) {
        String sql = "INSERT INTO federated_updates (device_id, local_weights, global_weights, round_number, accuracy) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, update.getDeviceId());
            ps.setString(2, update.getLocalWeights());
            ps.setString(3, update.getGlobalWeights());
            ps.setInt(4, update.getRoundNumber());
            ps.setDouble(5, update.getAccuracy());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[FederatedDAO] saveUpdate error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get updates by round number
     */
    public List<FederatedUpdate> getUpdatesByRound(int roundNumber) {
        List<FederatedUpdate> updates = new ArrayList<>();
        String sql = "SELECT * FROM federated_updates WHERE round_number = ? ORDER BY device_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roundNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                updates.add(extractUpdate(rs));
            }
        } catch (Exception e) {
            System.err.println("[FederatedDAO] getUpdatesByRound error: " + e.getMessage());
        }
        return updates;
    }

    /**
     * Get latest round number
     */
    public int getLatestRound() {
        String sql = "SELECT MAX(round_number) FROM federated_updates";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int round = rs.getInt(1);
                return rs.wasNull() ? 0 : round;
            }
        } catch (Exception e) {
            System.err.println("[FederatedDAO] getLatestRound error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get all updates ordered by round and device
     */
    public List<FederatedUpdate> getAllUpdates() {
        List<FederatedUpdate> updates = new ArrayList<>();
        String sql = "SELECT * FROM federated_updates ORDER BY round_number DESC, device_id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                updates.add(extractUpdate(rs));
            }
        } catch (Exception e) {
            System.err.println("[FederatedDAO] getAllUpdates error: " + e.getMessage());
        }
        return updates;
    }

    /**
     * Get total update count
     */
    public int getTotalUpdateCount() {
        String sql = "SELECT COUNT(*) FROM federated_updates";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[FederatedDAO] getTotalUpdateCount error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Extract FederatedUpdate from ResultSet
     */
    private FederatedUpdate extractUpdate(ResultSet rs) throws SQLException {
        FederatedUpdate update = new FederatedUpdate();
        update.setId(rs.getInt("id"));
        update.setDeviceId(rs.getString("device_id"));
        update.setLocalWeights(rs.getString("local_weights"));
        update.setGlobalWeights(rs.getString("global_weights"));
        update.setRoundNumber(rs.getInt("round_number"));
        update.setAccuracy(rs.getDouble("accuracy"));
        update.setCreatedAt(rs.getTimestamp("created_at"));
        return update;
    }
}
