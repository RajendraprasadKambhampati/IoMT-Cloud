package com.iomt.servlet;

import com.iomt.crypto.AESUtil;
import com.iomt.crypto.ABEPolicy;
import com.iomt.dao.FileDAO;
import com.iomt.dao.AccessRequestDAO;
import com.iomt.model.FileRecord;
import com.iomt.model.User;
import com.iomt.blockchain.Blockchain;
import com.iomt.anomaly.AnomalyDetector;
import com.iomt.trust.TrustManager;

import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * FileDownloadServlet - Handles file decryption and download
 * 1. Checks ABE policy against user attributes
 * 2. If authorized: decrypts file and serves for download
 * 3. If unauthorized: denies access and logs anomaly
 * 4. All access attempts are logged to blockchain
 */
@WebServlet("/download")
public class FileDownloadServlet extends HttpServlet {

    private FileDAO fileDAO = new FileDAO();
    private AccessRequestDAO accessRequestDAO = new AccessRequestDAO();
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

        User user = (User) session.getAttribute("user");
        String fileIdStr = request.getParameter("id");

        if (fileIdStr == null || fileIdStr.isEmpty()) {
            response.sendRedirect("files");
            return;
        }

        try {
            int fileId = Integer.parseInt(fileIdStr);
            FileRecord file = fileDAO.getFileById(fileId);

            if (file == null) {
                request.setAttribute("error", "File not found.");
                request.getRequestDispatcher("files.jsp").forward(request, response);
                return;
            }

            // Check trust score first
            if (!TrustManager.checkAccessByTrust(user)) {
                anomalyDetector.analyzeAndFlag(user.getId(), "ACCESS_DENIED",
                        "Low trust access attempt for file: " + file.getOriginalFilename(),
                        request.getRemoteAddr());
                blockchain.logFileAccess(user.getId(), user.getName(), file.getOriginalFilename(), false);
                trustManager.updateTrust(user.getId(), "ACCESS_DENIED", false, false);

                request.setAttribute("error", "Access denied: Your trust score is too low.");
                request.getRequestDispatcher("files.jsp").forward(request, response);
                return;
            }

            // Check if user is the owner
            boolean isOwner = file.getOwnerId() == user.getId();
            boolean isAdmin = "admin".equals(user.getRole());

            // Check ABE policy
            boolean policyMatch = ABEPolicy.checkAccess(user.getAttributes(), file.getPolicy());

            // Check if user has approved access request
            boolean hasApprovedAccess = accessRequestDAO.hasApprovedAccess(user.getId(), fileId);

            if (isOwner || isAdmin || policyMatch || hasApprovedAccess) {
                // ACCESS GRANTED - Decrypt and serve file
                SecretKey aesKey;

                if (isOwner || isAdmin) {
                    // Owner/Admin: extract key directly
                    String keyStr = file.getEncryptionKey().split(":::")[0];
                    aesKey = AESUtil.stringToKey(keyStr);
                } else {
                    // Other users: decrypt key via ABE policy
                    aesKey = ABEPolicy.decryptKeyWithAttributes(file.getEncryptionKey(), user.getAttributes());
                }

                if (aesKey == null) {
                    request.setAttribute("error", "Decryption failed: Key recovery error.");
                    request.getRequestDispatcher("files.jsp").forward(request, response);
                    return;
                }

                // Decrypt file data
                byte[] decryptedData = AESUtil.decrypt(file.getEncryptedData(), aesKey);

                // Verify integrity
                String hash = AESUtil.sha256Hash(decryptedData);
                if (!hash.equals(file.getFileHash())) {
                    request.setAttribute("error", "File integrity check failed! File may have been tampered.");
                    request.getRequestDispatcher("files.jsp").forward(request, response);
                    return;
                }

                // Log successful access
                anomalyDetector.analyzeAndFlag(user.getId(), "FILE_DOWNLOAD",
                        "Downloaded file: " + file.getOriginalFilename(),
                        request.getRemoteAddr());
                blockchain.logFileAccess(user.getId(), user.getName(), file.getOriginalFilename(), true);
                trustManager.updateTrust(user.getId(), "FILE_DOWNLOAD", true, false);

                // Serve file for download
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"" + file.getOriginalFilename() + "\"");
                response.setContentLength(decryptedData.length);

                OutputStream out = response.getOutputStream();
                out.write(decryptedData);
                out.flush();
                out.close();

            } else {
                // ACCESS DENIED
                anomalyDetector.analyzeAndFlag(user.getId(), "ACCESS_DENIED",
                        "Policy violation for file: " + file.getOriginalFilename() +
                        " | Policy: " + file.getPolicy() + " | User attrs: " + user.getAttributes(),
                        request.getRemoteAddr());
                blockchain.logFileAccess(user.getId(), user.getName(), file.getOriginalFilename(), false);
                trustManager.updateTrust(user.getId(), "ACCESS_DENIED", false, false);

                request.setAttribute("error", "Access denied: Your attributes do not match the file's encryption policy.");
                request.setAttribute("filePolicy", file.getPolicy());
                request.setAttribute("userAttributes", user.getAttributes());
                request.getRequestDispatcher("files.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("files");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Download error: " + e.getMessage());
            request.getRequestDispatcher("files.jsp").forward(request, response);
        }
    }
}
