package com.iomt.dao;

import com.iomt.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Data Access Object for User operations
 * Handles all database CRUD operations for the users table
 */
public class UserDAO {

    /**
     * Register a new user
     */
    public boolean register(User user) {
        String sql = "INSERT INTO users (name, email, password, role, department, clearance_level, attributes, trust_score, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getDepartment());
            ps.setInt(6, user.getClearanceLevel());
            ps.setString(7, user.getAttributes());
            ps.setDouble(8, user.getTrustScore());
            ps.setString(9, user.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[UserDAO] Register error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate user by email and hashed password
     */
    public User login(String email, String hashedPassword) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (Exception e) {
            System.err.println("[UserDAO] Login error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.err.println("[UserDAO] emailExists error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get user by ID
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (Exception e) {
            System.err.println("[UserDAO] getUserById error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (Exception e) {
            System.err.println("[UserDAO] getAllUsers error: " + e.getMessage());
        }
        return users;
    }

    /**
     * Get all non-admin users
     */
    public List<User> getNonAdminUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role != 'admin' ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (Exception e) {
            System.err.println("[UserDAO] getNonAdminUsers error: " + e.getMessage());
        }
        return users;
    }

    /**
     * Update user trust score
     */
    public boolean updateTrustScore(int userId, double newScore) {
        // Clamp score between 0 and 100
        newScore = Math.max(0, Math.min(100, newScore));
        String status = "active";
        if (newScore < 30) status = "blocked";
        else if (newScore < 50) status = "restricted";

        String sql = "UPDATE users SET trust_score = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newScore);
            ps.setString(2, status);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[UserDAO] updateTrustScore error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update user status
     */
    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[UserDAO] updateStatus error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get user count by role
     */
    public int getUserCountByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[UserDAO] getUserCountByRole error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total user count
     */
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[UserDAO] getTotalUserCount error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Extract User object from ResultSet
     */
    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setDepartment(rs.getString("department"));
        user.setClearanceLevel(rs.getInt("clearance_level"));
        user.setAttributes(rs.getString("attributes"));
        user.setTrustScore(rs.getDouble("trust_score"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
