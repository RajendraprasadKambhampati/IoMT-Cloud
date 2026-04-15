package com.iomt.model;

import java.sql.Timestamp;

/**
 * AccessRequest Model - Represents an access request to a file
 * Maps to the 'access_requests' table in the database
 */
public class AccessRequest {
    private int id;
    private int requesterId;
    private int fileId;
    private String status;         // pending, approved, denied
    private Timestamp requestedAt;
    private Timestamp resolvedAt;

    // Additional display fields (not in DB)
    private String requesterName;
    private String fileName;
    private String requesterRole;

    // Default constructor
    public AccessRequest() {
        this.status = "pending";
    }

    // Constructor for creating a new request
    public AccessRequest(int requesterId, int fileId) {
        this();
        this.requesterId = requesterId;
        this.fileId = fileId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    public int getFileId() { return fileId; }
    public void setFileId(int fileId) { this.fileId = fileId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Timestamp requestedAt) { this.requestedAt = requestedAt; }

    public Timestamp getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getRequesterRole() { return requesterRole; }
    public void setRequesterRole(String requesterRole) { this.requesterRole = requesterRole; }

    @Override
    public String toString() {
        return "AccessRequest{id=" + id + ", requesterId=" + requesterId +
               ", fileId=" + fileId + ", status='" + status + "'}";
    }
}
