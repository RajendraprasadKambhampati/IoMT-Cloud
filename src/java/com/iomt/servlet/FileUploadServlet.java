package com.iomt.servlet;

import com.iomt.crypto.AESUtil;
import com.iomt.crypto.ABEPolicy;
import com.iomt.dao.FileDAO;
import com.iomt.model.FileRecord;
import com.iomt.model.User;
import com.iomt.blockchain.Blockchain;
import com.iomt.anomaly.AnomalyDetector;
import com.iomt.trust.TrustManager;

import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * FileUploadServlet - Handles encrypted file uploads
 * 1. Receives file + policy parameters
 * 2. Generates AES key and encrypts file
 * 3. Wraps key with ABE policy
 * 4. Stores encrypted file in database
 * 5. Logs upload to blockchain
 */
@WebServlet("/upload")
@MultipartConfig(
    maxFileSize = 52428800,      // 50MB
    maxRequestSize = 52428800,
    fileSizeThreshold = 1048576  // 1MB
)
public class FileUploadServlet extends HttpServlet {

    private FileDAO fileDAO = new FileDAO();
    private Blockchain blockchain = new Blockchain();
    private AnomalyDetector anomalyDetector = new AnomalyDetector();
    private TrustManager trustManager = new TrustManager();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        request.getRequestDispatcher("upload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            // Get uploaded file
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("error", "Please select a file to upload.");
                request.getRequestDispatcher("upload.jsp").forward(request, response);
                return;
            }

            String originalFilename = getFileName(filePart);
            if (originalFilename == null || originalFilename.isEmpty()) {
                originalFilename = "unknown_file";
            }

            // Get policy parameters
            String policyRole = request.getParameter("policyRole");
            String policyDept = request.getParameter("policyDept");
            String policyLevelStr = request.getParameter("policyLevel");
            int policyLevel = 0;
            try {
                policyLevel = Integer.parseInt(policyLevelStr);
            } catch (NumberFormatException e) {
                policyLevel = 0;
            }

            // Build ABE policy
            String policy = ABEPolicy.buildPolicy(policyRole, policyDept, policyLevel);
            if (policy.isEmpty()) {
                policy = "role=" + user.getRole(); // Default: same role as uploader
            }

            // Read file data
            InputStream fileContent = filePart.getInputStream();
            byte[] plainData = fileContent.readAllBytes();
            fileContent.close();

            // Step 1: Generate AES key
            SecretKey aesKey = AESUtil.generateKey();

            // Step 2: Encrypt file data
            byte[] encryptedData = AESUtil.encrypt(plainData, aesKey);

            // Step 3: Wrap AES key with ABE policy
            String encryptedKey = ABEPolicy.encryptKeyWithPolicy(aesKey, policy);

            // Step 4: Calculate file hash for integrity
            String fileHash = AESUtil.sha256Hash(plainData);

            // Step 5: Generate unique filename
            String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            // Step 6: Create file record and store in DB
            FileRecord fileRecord = new FileRecord(
                user.getId(), storedFilename, originalFilename,
                encryptedData, encryptedKey, policy, fileHash, plainData.length
            );

            int fileId = fileDAO.uploadFile(fileRecord);

            if (fileId > 0) {
                // Step 7: Log to blockchain
                blockchain.logFileUpload(user.getId(), user.getName(), originalFilename, fileHash);

                // Step 8: Log activity
                anomalyDetector.analyzeAndFlag(user.getId(), "FILE_UPLOAD",
                        "Uploaded file: " + originalFilename + " (ID: " + fileId + ")",
                        request.getRemoteAddr());

                // Step 9: Update trust (reward)
                trustManager.updateTrust(user.getId(), "FILE_UPLOAD", true, false);

                request.setAttribute("success", "File uploaded and encrypted successfully! (ID: " + fileId + ")");
                request.setAttribute("fileHash", fileHash);
                request.setAttribute("policy", policy);
            } else {
                request.setAttribute("error", "Failed to store encrypted file.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Upload error: " + e.getMessage());
        }

        request.getRequestDispatcher("upload.jsp").forward(request, response);
    }

    /**
     * Extract filename from multipart header
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            for (String token : contentDisposition.split(";")) {
                if (token.trim().startsWith("filename")) {
                    String name = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                    // Handle path in filename (Windows uploads may include full path)
                    int lastSlash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
                    if (lastSlash >= 0) {
                        name = name.substring(lastSlash + 1);
                    }
                    return name;
                }
            }
        }
        return null;
    }
}
