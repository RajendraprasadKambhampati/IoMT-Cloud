package com.iomt.servlet;

import com.iomt.dao.FileDAO;
import com.iomt.model.FileRecord;
import com.iomt.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * FileListServlet - Display files accessible to the user
 * Shows own files and all files (for admin)
 */
@WebServlet("/files")
public class FileListServlet extends HttpServlet {

    private FileDAO fileDAO = new FileDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");

        List<FileRecord> myFiles = fileDAO.getFilesByOwner(user.getId());
        List<FileRecord> allFiles = fileDAO.getAllFiles();

        request.setAttribute("myFiles", myFiles);
        request.setAttribute("allFiles", allFiles);

        request.getRequestDispatcher("files.jsp").forward(request, response);
    }
}
