package com.iomt.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * DBConnection - Singleton database connection manager
 * Reads configuration from db.properties
 */
public class DBConnection {

    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER;

    // Static initializer - load properties once
    static {
        try {
            // Priority 1: Environment variables (for cloud deployment like Railway)
            String envUrl = System.getenv("DB_URL");
            String envUser = System.getenv("DB_USER");
            String envPass = System.getenv("DB_PASS");

            if (envUrl != null && !envUrl.isEmpty()) {
                URL = envUrl;
                USERNAME = envUser != null ? envUser : "root";
                PASSWORD = envPass != null ? envPass : "";
                DRIVER = "com.mysql.cj.jdbc.Driver";
                System.out.println("[DBConnection] Using environment variables for DB config.");
            } else {
                // Priority 2: db.properties file (for local Tomcat development)
                Properties props = new Properties();
                InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
                if (is == null) {
                    // Fallback to default values
                    URL = "jdbc:mysql://localhost:3306/iomt_cloud?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                    USERNAME = "root";
                    PASSWORD = "";
                    DRIVER = "com.mysql.cj.jdbc.Driver";
                } else {
                    props.load(is);
                    URL = props.getProperty("db.url");
                    USERNAME = props.getProperty("db.username");
                    PASSWORD = props.getProperty("db.password");
                    DRIVER = props.getProperty("db.driver");
                    is.close();
                }
            }
            Class.forName(DRIVER);
            System.out.println("[DBConnection] MySQL driver loaded successfully. URL: " + URL);
        } catch (Exception e) {
            System.err.println("[DBConnection] Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get a new database connection
     * @return Connection object
     * @throws Exception if connection fails
     */
    public static Connection getConnection() throws Exception {
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        return conn;
    }

    /**
     * Close connection safely
     */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Test database connectivity
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean valid = conn != null && !conn.isClosed();
            closeConnection(conn);
            System.out.println("[DBConnection] Connection test: " + (valid ? "SUCCESS" : "FAILED"));
            return valid;
        } catch (Exception e) {
            System.err.println("[DBConnection] Connection test FAILED: " + e.getMessage());
            return false;
        }
    }
}
