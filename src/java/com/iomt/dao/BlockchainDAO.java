package com.iomt.dao;

import com.iomt.model.Block;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BlockchainDAO - Data Access Object for Blockchain operations
 * Manages the immutable blockchain log storage
 */
public class BlockchainDAO {

    /**
     * Add a new block to the blockchain
     */
    public boolean addBlock(Block block) {
        String sql = "INSERT INTO blockchain (block_index, timestamp, data, prev_hash, hash, nonce) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, block.getBlockIndex());
            ps.setLong(2, block.getTimestamp());
            ps.setString(3, block.getData());
            ps.setString(4, block.getPrevHash());
            ps.setString(5, block.getHash());
            ps.setInt(6, block.getNonce());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[BlockchainDAO] addBlock error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all blocks in order
     */
    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        String sql = "SELECT * FROM blockchain ORDER BY block_index ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                blocks.add(extractBlock(rs));
            }
        } catch (Exception e) {
            System.err.println("[BlockchainDAO] getAllBlocks error: " + e.getMessage());
        }
        return blocks;
    }

    /**
     * Get the latest block (highest index)
     */
    public Block getLatestBlock() {
        String sql = "SELECT * FROM blockchain ORDER BY block_index DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return extractBlock(rs);
            }
        } catch (Exception e) {
            System.err.println("[BlockchainDAO] getLatestBlock error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get total block count
     */
    public int getBlockCount() {
        String sql = "SELECT COUNT(*) FROM blockchain";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[BlockchainDAO] getBlockCount error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get recent blocks (for dashboard)
     */
    public List<Block> getRecentBlocks(int limit) {
        List<Block> blocks = new ArrayList<>();
        String sql = "SELECT * FROM blockchain ORDER BY block_index DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                blocks.add(extractBlock(rs));
            }
        } catch (Exception e) {
            System.err.println("[BlockchainDAO] getRecentBlocks error: " + e.getMessage());
        }
        return blocks;
    }

    /**
     * Extract Block from ResultSet
     */
    private Block extractBlock(ResultSet rs) throws SQLException {
        Block block = new Block();
        block.setId(rs.getInt("id"));
        block.setBlockIndex(rs.getInt("block_index"));
        block.setTimestamp(rs.getLong("timestamp"));
        block.setData(rs.getString("data"));
        block.setPrevHash(rs.getString("prev_hash"));
        block.setHash(rs.getString("hash"));
        block.setNonce(rs.getInt("nonce"));
        return block;
    }
}
