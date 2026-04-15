package com.iomt.dao;

import com.iomt.model.FileRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileDAO - Data Access Object for File operations
 * Handles encrypted file storage and retrieval
 */
public class FileDAO {

    /**
     * Upload/store a new encrypted file
     */
    public int uploadFile(FileRecord file) {
        String sql = "INSERT INTO files (owner_id, filename, original_filename, encrypted_data, encryption_key, policy, file_hash, file_size) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, file.getOwnerId());
            ps.setString(2, file.getFilename());
            ps.setString(3, file.getOriginalFilename());
            ps.setBytes(4, file.getEncryptedData());
            ps.setString(5, file.getEncryptionKey());
            ps.setString(6, file.getPolicy());
            ps.setString(7, file.getFileHash());
            ps.setLong(8, file.getFileSize());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("[FileDAO] uploadFile error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Get file by ID
     */
    public FileRecord getFileById(int id) {
        String sql = "SELECT f.*, u.name as owner_name FROM files f JOIN users u ON f.owner_id = u.id WHERE f.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractFileRecord(rs);
            }
        } catch (Exception e) {
            System.err.println("[FileDAO] getFileById error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get files owned by a specific user
     */
    public List<FileRecord> getFilesByOwner(int ownerId) {
        List<FileRecord> files = new ArrayList<>();
        String sql = "SELECT f.*, u.name as owner_name FROM files f JOIN users u ON f.owner_id = u.id WHERE f.owner_id = ? ORDER BY f.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                files.add(extractFileRecordLight(rs));
            }
        } catch (Exception e) {
            System.err.println("[FileDAO] getFilesByOwner error: " + e.getMessage());
        }
        return files;
    }

    /**
     * Get all files (admin view) - without blob data for performance
     */
    public List<FileRecord> getAllFiles() {
        List<FileRecord> files = new ArrayList<>();
        String sql = "SELECT f.id, f.owner_id, f.filename, f.original_filename, f.encryption_key, f.policy, f.file_hash, f.file_size, f.created_at, u.name as owner_name FROM files f JOIN users u ON f.owner_id = u.id ORDER BY f.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                files.add(extractFileRecordLight(rs));
            }
        } catch (Exception e) {
            System.err.println("[FileDAO] getAllFiles error: " + e.getMessage());
        }
        return files;
    }

    /**
     * Get total file count
     */
    public int getTotalFileCount() {
        String sql = "SELECT COUNT(*) FROM files";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[FileDAO] getTotalFileCount error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get file count by owner
     */
    public int getFileCountByOwner(int ownerId) {
        String sql = "SELECT COUNT(*) FROM files WHERE owner_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[FileDAO] getFileCountByOwner error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Delete a file
     */
    public boolean deleteFile(int fileId) {
        String sql = "DELETE FROM files WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fileId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[FileDAO] deleteFile error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract FileRecord from ResultSet (with blob data)
     */
    private FileRecord extractFileRecord(ResultSet rs) throws SQLException {
        FileRecord file = new FileRecord();
        file.setId(rs.getInt("id"));
        file.setOwnerId(rs.getInt("owner_id"));
        file.setFilename(rs.getString("filename"));
        file.setOriginalFilename(rs.getString("original_filename"));
        file.setEncryptedData(rs.getBytes("encrypted_data"));
        file.setEncryptionKey(rs.getString("encryption_key"));
        file.setPolicy(rs.getString("policy"));
        file.setFileHash(rs.getString("file_hash"));
        file.setFileSize(rs.getLong("file_size"));
        file.setCreatedAt(rs.getTimestamp("created_at"));
        try { file.setOwnerName(rs.getString("owner_name")); } catch (SQLException ignored) {}
        return file;
    }

    /**
     * Extract FileRecord from ResultSet (without blob data - for listings)
     */
    private FileRecord extractFileRecordLight(ResultSet rs) throws SQLException {
        FileRecord file = new FileRecord();
        file.setId(rs.getInt("id"));
        file.setOwnerId(rs.getInt("owner_id"));
        file.setFilename(rs.getString("filename"));
        file.setOriginalFilename(rs.getString("original_filename"));
        file.setEncryptionKey(rs.getString("encryption_key"));
        file.setPolicy(rs.getString("policy"));
        file.setFileHash(rs.getString("file_hash"));
        file.setFileSize(rs.getLong("file_size"));
        file.setCreatedAt(rs.getTimestamp("created_at"));
        try { file.setOwnerName(rs.getString("owner_name")); } catch (SQLException ignored) {}
        return file;
    }
}
