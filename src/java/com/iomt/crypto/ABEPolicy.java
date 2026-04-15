package com.iomt.crypto;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

/**
 * ABEPolicy - Attribute-Based Encryption Policy Engine
 * Simulates Ciphertext-Policy ABE (CP-ABE) using attribute matching
 *
 * Policy format: "role=doctor AND dept=cardiology AND level>=2"
 * User attributes: "role=doctor;dept=cardiology;level=3"
 *
 * The AES key is only accessible if user attributes satisfy the policy
 */
public class ABEPolicy {

    /**
     * Check if user attributes satisfy the given policy
     * Supports AND/OR operators and >= comparisons for levels
     *
     * @param userAttributes User's attributes string (semicolon-separated)
     * @param policy Policy string with AND/OR conditions
     * @return true if access should be granted
     */
    public static boolean checkAccess(String userAttributes, String policy) {
        if (userAttributes == null || policy == null) return false;
        if (policy.trim().isEmpty()) return true;

        // Parse user attributes into a map
        Map<String, String> attrMap = parseAttributes(userAttributes);

        // Handle OR conditions first (split by OR, any group must match)
        String[] orGroups = policy.split("\\s+OR\\s+");
        for (String group : orGroups) {
            if (evaluateAndGroup(attrMap, group.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evaluate an AND group of conditions
     */
    private static boolean evaluateAndGroup(Map<String, String> attrMap, String andGroup) {
        String[] conditions = andGroup.split("\\s+AND\\s+");
        for (String condition : conditions) {
            if (!evaluateCondition(attrMap, condition.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Evaluate a single condition against user attributes
     * Supports: =, >=, <=, >, <
     */
    private static boolean evaluateCondition(Map<String, String> attrMap, String condition) {
        condition = condition.trim();

        // Handle >= operator
        if (condition.contains(">=")) {
            String[] parts = condition.split(">=");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                String userValue = attrMap.get(key);
                if (userValue == null) return false;
                try {
                    return Integer.parseInt(userValue) >= Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return userValue.compareTo(value) >= 0;
                }
            }
        }

        // Handle <= operator
        if (condition.contains("<=")) {
            String[] parts = condition.split("<=");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                String userValue = attrMap.get(key);
                if (userValue == null) return false;
                try {
                    return Integer.parseInt(userValue) <= Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return userValue.compareTo(value) <= 0;
                }
            }
        }

        // Handle = operator (exact match)
        if (condition.contains("=")) {
            String[] parts = condition.split("=");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                String userValue = attrMap.get(key);
                return value.equalsIgnoreCase(userValue);
            }
        }

        return false;
    }

    /**
     * Parse attribute string into key-value map
     * Format: "role=doctor;dept=cardiology;level=3"
     */
    public static Map<String, String> parseAttributes(String attributes) {
        Map<String, String> map = new HashMap<>();
        if (attributes == null || attributes.isEmpty()) return map;

        String[] pairs = attributes.split(";");
        for (String pair : pairs) {
            String[] kv = pair.trim().split("=");
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }
        return map;
    }

    /**
     * Encrypt AES key with policy - wraps key with policy metadata
     * In real ABE, the key would be mathematically bound to the policy.
     * Here we simulate by encoding key + policy together.
     *
     * @param aesKey The AES secret key to protect
     * @param policy The access policy string
     * @return Encoded string containing key and policy
     */
    public static String encryptKeyWithPolicy(SecretKey aesKey, String policy) {
        String keyStr = AESUtil.keyToString(aesKey);
        // Format: BASE64(key):::POLICY
        return keyStr + ":::" + policy;
    }

    /**
     * Decrypt AES key with user attributes - only returns key if policy matches
     *
     * @param encryptedKey The policy-protected key string
     * @param userAttributes User's attributes
     * @return SecretKey if authorized, null otherwise
     */
    public static SecretKey decryptKeyWithAttributes(String encryptedKey, String userAttributes) {
        if (encryptedKey == null || !encryptedKey.contains(":::")) return null;

        String[] parts = encryptedKey.split(":::", 2);
        String keyStr = parts[0];
        String policy = parts[1];

        // Check if user attributes satisfy the policy
        if (checkAccess(userAttributes, policy)) {
            return AESUtil.stringToKey(keyStr);
        }

        return null; // Access denied - attributes don't match policy
    }

    /**
     * Build a policy string for file upload
     * @param role Required role (or "any")
     * @param department Required department (or "any")
     * @param minLevel Minimum clearance level (0 for no restriction)
     * @return Policy string
     */
    public static String buildPolicy(String role, String department, int minLevel) {
        StringBuilder policy = new StringBuilder();
        boolean hasCondition = false;

        if (role != null && !role.isEmpty() && !role.equals("any")) {
            policy.append("role=").append(role);
            hasCondition = true;
        }

        if (department != null && !department.isEmpty() && !department.equals("any")) {
            if (hasCondition) policy.append(" AND ");
            policy.append("dept=").append(department);
            hasCondition = true;
        }

        if (minLevel > 0) {
            if (hasCondition) policy.append(" AND ");
            policy.append("level>=").append(minLevel);
        }

        return policy.toString();
    }

    /**
     * Get a human-readable description of a policy
     */
    public static String getPolicyDescription(String policy) {
        if (policy == null || policy.isEmpty()) return "No restrictions";
        return policy.replace(" AND ", " & ").replace(" OR ", " | ");
    }
}
