-- ============================================================
-- Trust-Aware Secure Federated Cloud Storage for IoMT
-- Database Schema - MySQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS iomt_cloud;
USE iomt_cloud;

-- ============================================================
-- Users Table
-- Stores all system users: doctors, hospitals, devices, admin
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('doctor', 'hospital', 'device', 'admin') NOT NULL,
    department VARCHAR(100),
    clearance_level INT DEFAULT 1,
    attributes VARCHAR(500),           -- Format: "role=doctor;dept=cardiology;level=3"
    trust_score DOUBLE DEFAULT 100.0,
    status ENUM('active', 'restricted', 'blocked') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Files Table
-- Stores encrypted medical data files
-- ============================================================
CREATE TABLE IF NOT EXISTS files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    encrypted_data LONGBLOB NOT NULL,
    encryption_key VARCHAR(512),       -- AES key encrypted with policy
    policy VARCHAR(500) NOT NULL,      -- ABE policy: "role=doctor AND dept=cardiology"
    file_hash VARCHAR(128) NOT NULL,   -- SHA-256 hash for integrity
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Access Requests Table
-- Tracks access requests between users for files
-- ============================================================
CREATE TABLE IF NOT EXISTS access_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    requester_id INT NOT NULL,
    file_id INT NOT NULL,
    status ENUM('pending', 'approved', 'denied') DEFAULT 'pending',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Blockchain Table
-- Immutable log chain for audit trail
-- ============================================================
CREATE TABLE IF NOT EXISTS blockchain (
    id INT AUTO_INCREMENT PRIMARY KEY,
    block_index INT NOT NULL,
    timestamp BIGINT NOT NULL,
    data TEXT NOT NULL,
    prev_hash VARCHAR(128) NOT NULL,
    hash VARCHAR(128) NOT NULL,
    nonce INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Logs Table
-- System activity logs with anomaly flagging
-- ============================================================
CREATE TABLE IF NOT EXISTS logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(255) NOT NULL,
    details TEXT,
    ip_address VARCHAR(50),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    anomaly_flag BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Federated Learning Updates Table
-- Stores local and global model weights per round
-- ============================================================
CREATE TABLE IF NOT EXISTS federated_updates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(100) NOT NULL,
    local_weights TEXT NOT NULL,        -- JSON array of weight values
    global_weights TEXT,                -- Aggregated global weights
    round_number INT NOT NULL,
    accuracy DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Insert Genesis Block
-- ============================================================
INSERT INTO blockchain (block_index, timestamp, data, prev_hash, hash, nonce)
VALUES (0, UNIX_TIMESTAMP() * 1000, '{"event":"Genesis Block","message":"IoMT Blockchain Initialized"}', '0',
        SHA2(CONCAT('0', UNIX_TIMESTAMP() * 1000, '{"event":"Genesis Block","message":"IoMT Blockchain Initialized"}', '0'), 256), 0);

-- ============================================================
-- Sample Data: Admin User (password: admin123 - SHA-256)
-- ============================================================
INSERT INTO users (name, email, password, role, department, clearance_level, attributes, trust_score)
VALUES ('System Admin', 'admin@iomt.com',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        'admin', 'administration', 5,
        'role=admin;dept=administration;level=5', 100.0);

-- Sample Doctor
INSERT INTO users (name, email, password, role, department, clearance_level, attributes, trust_score)
VALUES ('Dr. Sarah Smith', 'doctor@iomt.com',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        'doctor', 'cardiology', 3,
        'role=doctor;dept=cardiology;level=3', 95.0);

-- Sample Hospital
INSERT INTO users (name, email, password, role, department, clearance_level, attributes, trust_score)
VALUES ('City General Hospital', 'hospital@iomt.com',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        'hospital', 'general', 4,
        'role=hospital;dept=general;level=4', 90.0);

-- Sample Device
INSERT INTO users (name, email, password, role, department, clearance_level, attributes, trust_score)
VALUES ('ECG Monitor Alpha', 'device@iomt.com',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        'device', 'cardiology', 1,
        'role=device;dept=cardiology;level=1', 85.0);

-- ============================================================
-- Done! All tables and sample data created.
-- ============================================================
