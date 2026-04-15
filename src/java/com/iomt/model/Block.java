package com.iomt.model;

/**
 * Block Model - Represents a single block in the blockchain
 * Maps to the 'blockchain' table in the database
 */
public class Block {
    private int id;
    private int blockIndex;
    private long timestamp;
    private String data;        // JSON-formatted event data
    private String prevHash;    // Hash of the previous block
    private String hash;        // Hash of this block
    private int nonce;

    // Default constructor
    public Block() {}

    // Constructor for creating a new block
    public Block(int blockIndex, long timestamp, String data, String prevHash, String hash) {
        this.blockIndex = blockIndex;
        this.timestamp = timestamp;
        this.data = data;
        this.prevHash = prevHash;
        this.hash = hash;
        this.nonce = 0;
    }

    // Full constructor
    public Block(int blockIndex, long timestamp, String data, String prevHash, String hash, int nonce) {
        this(blockIndex, timestamp, data, prevHash, hash);
        this.nonce = nonce;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBlockIndex() { return blockIndex; }
    public void setBlockIndex(int blockIndex) { this.blockIndex = blockIndex; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getPrevHash() { return prevHash; }
    public void setPrevHash(String prevHash) { this.prevHash = prevHash; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public int getNonce() { return nonce; }
    public void setNonce(int nonce) { this.nonce = nonce; }

    /**
     * Get formatted timestamp string
     */
    public String getFormattedTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestamp));
    }

    /**
     * Get short hash for display (first 16 chars)
     */
    public String getShortHash() {
        return hash != null && hash.length() > 16 ? hash.substring(0, 16) + "..." : hash;
    }

    /**
     * Get short prev hash for display
     */
    public String getShortPrevHash() {
        return prevHash != null && prevHash.length() > 16 ? prevHash.substring(0, 16) + "..." : prevHash;
    }

    @Override
    public String toString() {
        return "Block{index=" + blockIndex + ", hash='" + getShortHash() +
               "', prevHash='" + getShortPrevHash() + "'}";
    }
}
