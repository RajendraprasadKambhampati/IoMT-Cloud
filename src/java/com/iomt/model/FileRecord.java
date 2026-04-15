package com.iomt.model;

import java.sql.Timestamp;

/**
 * FileRecord Model - Represents an encrypted file stored in cloud storage
 * Maps to the 'files' table in the database
 */
public class FileRecord {
    private int id;
    private int ownerId;
    private String filename;           // System-generated filename
    private String originalFilename;   // Original uploaded filename
    private byte[] encryptedData;      // AES-encrypted file content
    private String encryptionKey;      // Encrypted AES key (with policy)
    private String policy;             // ABE policy: "role=doctor AND dept=cardiology"
    private String fileHash;           // SHA-256 hash for file integrity
    private long fileSize;
    private Timestamp createdAt;

    // Additional fields for display (not in DB)
    private String ownerName;

    // Default constructor
    public FileRecord() {}

    // Constructor for uploading
    public FileRecord(int ownerId, String filename, String originalFilename,
                      byte[] encryptedData, String encryptionKey, String policy,
                      String fileHash, long fileSize) {
        this.ownerId = ownerId;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.encryptedData = encryptedData;
        this.encryptionKey = encryptionKey;
        this.policy = policy;
        this.fileHash = fileHash;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public byte[] getEncryptedData() { return encryptedData; }
    public void setEncryptedData(byte[] encryptedData) { this.encryptedData = encryptedData; }

    public String getEncryptionKey() { return encryptionKey; }
    public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }

    public String getPolicy() { return policy; }
    public void setPolicy(String policy) { this.policy = policy; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    /**
     * Get human-readable file size
     */
    public String getFormattedSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        return String.format("%.1f GB", fileSize / (1024.0 * 1024 * 1024));
    }

    @Override
    public String toString() {
        return "FileRecord{id=" + id + ", originalFilename='" + originalFilename +
               "', policy='" + policy + "', hash='" + fileHash + "'}";
    }
}
