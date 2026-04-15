package com.iomt.model;

import java.sql.Timestamp;

/**
 * FederatedUpdate Model - Represents a federated learning update from a device
 * Maps to the 'federated_updates' table in the database
 */
public class FederatedUpdate {
    private int id;
    private String deviceId;
    private String localWeights;    // JSON array: "[0.12, 0.45, 0.78, ...]"
    private String globalWeights;   // Aggregated weights
    private int roundNumber;
    private double accuracy;
    private Timestamp createdAt;

    // Default constructor
    public FederatedUpdate() {}

    // Constructor for creating a new update
    public FederatedUpdate(String deviceId, String localWeights, int roundNumber, double accuracy) {
        this.deviceId = deviceId;
        this.localWeights = localWeights;
        this.roundNumber = roundNumber;
        this.accuracy = accuracy;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getLocalWeights() { return localWeights; }
    public void setLocalWeights(String localWeights) { this.localWeights = localWeights; }

    public String getGlobalWeights() { return globalWeights; }
    public void setGlobalWeights(String globalWeights) { this.globalWeights = globalWeights; }

    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Get formatted accuracy as percentage
     */
    public String getFormattedAccuracy() {
        return String.format("%.2f%%", accuracy * 100);
    }

    @Override
    public String toString() {
        return "FederatedUpdate{deviceId='" + deviceId + "', round=" + roundNumber +
               ", accuracy=" + getFormattedAccuracy() + "}";
    }
}
