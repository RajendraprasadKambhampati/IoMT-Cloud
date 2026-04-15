package com.iomt.model;

import java.sql.Timestamp;

/**
 * User Model - Represents system users (doctors, hospitals, devices, admins)
 * Maps to the 'users' table in the database
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;          // doctor, hospital, device, admin
    private String department;
    private int clearanceLevel;
    private String attributes;    // Format: "role=doctor;dept=cardiology;level=3"
    private double trustScore;
    private String status;        // active, restricted, blocked
    private Timestamp createdAt;

    // Default constructor
    public User() {
        this.trustScore = 100.0;
        this.status = "active";
        this.clearanceLevel = 1;
    }

    // Parameterized constructor
    public User(String name, String email, String password, String role,
                String department, int clearanceLevel) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.department = department;
        this.clearanceLevel = clearanceLevel;
        this.attributes = "role=" + role + ";dept=" + department + ";level=" + clearanceLevel;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getClearanceLevel() { return clearanceLevel; }
    public void setClearanceLevel(int clearanceLevel) { this.clearanceLevel = clearanceLevel; }

    public String getAttributes() { return attributes; }
    public void setAttributes(String attributes) { this.attributes = attributes; }

    public double getTrustScore() { return trustScore; }
    public void setTrustScore(double trustScore) { this.trustScore = trustScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Build attributes string from role, department, and clearance level
     */
    public void buildAttributes() {
        this.attributes = "role=" + this.role + ";dept=" + this.department + ";level=" + this.clearanceLevel;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', role='" + role +
               "', trustScore=" + trustScore + ", status='" + status + "'}";
    }
}
