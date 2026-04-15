package com.iomt.blockchain;

import com.iomt.dao.BlockchainDAO;
import com.iomt.model.Block;
import java.security.MessageDigest;
import java.util.List;

/**
 * Blockchain - Simple blockchain simulation for immutable audit logging
 * Each block contains: index, timestamp, data, previous hash, current hash
 * Hash chaining ensures tamper-proof logs
 */
public class Blockchain {

    private BlockchainDAO blockchainDAO;

    public Blockchain() {
        this.blockchainDAO = new BlockchainDAO();
    }

    public Blockchain(BlockchainDAO dao) {
        this.blockchainDAO = dao;
    }

    /**
     * Calculate SHA-256 hash of a block's content
     * Hash = SHA256(index + timestamp + data + prevHash + nonce)
     */
    public static String calculateHash(int index, long timestamp, String data, String prevHash, int nonce) {
        try {
            String input = index + "" + timestamp + data + prevHash + nonce;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash calculation failed", e);
        }
    }

    /**
     * Calculate hash for an existing block
     */
    public static String calculateHash(Block block) {
        return calculateHash(block.getBlockIndex(), block.getTimestamp(),
                block.getData(), block.getPrevHash(), block.getNonce());
    }

    /**
     * Add a new block to the blockchain
     * @param data JSON-formatted event data
     * @return The newly created block, or null if failed
     */
    public Block addBlock(String data) {
        try {
            // Get the latest block to chain from
            Block latestBlock = blockchainDAO.getLatestBlock();

            int newIndex;
            String prevHash;

            if (latestBlock == null) {
                // No blocks exist - create genesis block first
                Block genesis = createGenesisBlock();
                blockchainDAO.addBlock(genesis);
                newIndex = 1;
                prevHash = genesis.getHash();
            } else {
                newIndex = latestBlock.getBlockIndex() + 1;
                prevHash = latestBlock.getHash();
            }

            long timestamp = System.currentTimeMillis();
            int nonce = 0;
            String hash = calculateHash(newIndex, timestamp, data, prevHash, nonce);

            Block newBlock = new Block(newIndex, timestamp, data, prevHash, hash, nonce);
            boolean saved = blockchainDAO.addBlock(newBlock);

            if (saved) {
                System.out.println("[Blockchain] Block #" + newIndex + " added successfully.");
                return newBlock;
            }
        } catch (Exception e) {
            System.err.println("[Blockchain] addBlock error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Create the genesis block (first block in chain)
     */
    public static Block createGenesisBlock() {
        int index = 0;
        long timestamp = System.currentTimeMillis();
        String data = "{\"event\":\"Genesis Block\",\"message\":\"IoMT Blockchain Initialized\"}";
        String prevHash = "0";
        int nonce = 0;
        String hash = calculateHash(index, timestamp, data, prevHash, nonce);

        return new Block(index, timestamp, data, prevHash, hash, nonce);
    }

    /**
     * Validate the entire blockchain integrity
     * Checks that each block's hash is correct and chains to the previous block
     * @return true if the blockchain is valid (no tampering)
     */
    public boolean validateChain() {
        List<Block> blocks = blockchainDAO.getAllBlocks();

        if (blocks.isEmpty()) return true;

        // Verify genesis block
        Block genesis = blocks.get(0);
        if (!genesis.getPrevHash().equals("0")) {
            System.err.println("[Blockchain] Invalid genesis block");
            return false;
        }

        // Verify each subsequent block
        for (int i = 1; i < blocks.size(); i++) {
            Block current = blocks.get(i);
            Block previous = blocks.get(i - 1);

            // Verify hash chain
            if (!current.getPrevHash().equals(previous.getHash())) {
                System.err.println("[Blockchain] Chain broken at block #" + current.getBlockIndex());
                return false;
            }

            // Verify current block hash
            String recalculatedHash = calculateHash(current);
            if (!current.getHash().equals(recalculatedHash)) {
                System.err.println("[Blockchain] Invalid hash at block #" + current.getBlockIndex());
                return false;
            }
        }

        System.out.println("[Blockchain] Chain validation: VALID (" + blocks.size() + " blocks)");
        return true;
    }

    /**
     * Get all blocks
     */
    public List<Block> getAllBlocks() {
        return blockchainDAO.getAllBlocks();
    }

    /**
     * Get block count
     */
    public int getBlockCount() {
        return blockchainDAO.getBlockCount();
    }

    /**
     * Create a log entry for file upload
     */
    public Block logFileUpload(int userId, String userName, String filename, String fileHash) {
        String data = String.format(
            "{\"event\":\"FILE_UPLOAD\",\"userId\":%d,\"userName\":\"%s\",\"filename\":\"%s\",\"fileHash\":\"%s\",\"timestamp\":\"%s\"}",
            userId, userName, filename, fileHash,
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        );
        return addBlock(data);
    }

    /**
     * Create a log entry for file access
     */
    public Block logFileAccess(int userId, String userName, String filename, boolean granted) {
        String data = String.format(
            "{\"event\":\"FILE_ACCESS\",\"userId\":%d,\"userName\":\"%s\",\"filename\":\"%s\",\"granted\":%b,\"timestamp\":\"%s\"}",
            userId, userName, filename, granted,
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        );
        return addBlock(data);
    }

    /**
     * Create a log entry for trust score change
     */
    public Block logTrustChange(int userId, String userName, double oldScore, double newScore, String reason) {
        String data = String.format(
            "{\"event\":\"TRUST_CHANGE\",\"userId\":%d,\"userName\":\"%s\",\"oldScore\":%.1f,\"newScore\":%.1f,\"reason\":\"%s\",\"timestamp\":\"%s\"}",
            userId, userName, oldScore, newScore, reason,
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        );
        return addBlock(data);
    }

    /**
     * Create a log entry for anomaly detection
     */
    public Block logAnomaly(int userId, String userName, String anomalyType) {
        String data = String.format(
            "{\"event\":\"ANOMALY_DETECTED\",\"userId\":%d,\"userName\":\"%s\",\"type\":\"%s\",\"timestamp\":\"%s\"}",
            userId, userName, anomalyType,
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        );
        return addBlock(data);
    }
}
