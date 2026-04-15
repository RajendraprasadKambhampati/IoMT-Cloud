package com.iomt.servlet;

import com.iomt.blockchain.Blockchain;
import com.iomt.model.Block;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * BlockchainServlet - Displays blockchain logs
 * Shows all blocks with hash chaining visualization
 */
@WebServlet("/blockchain")
public class BlockchainServlet extends HttpServlet {

    private Blockchain blockchain = new Blockchain();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        // Get all blocks
        List<Block> blocks = blockchain.getAllBlocks();

        // Validate chain
        boolean chainValid = blockchain.validateChain();

        // Stats
        int blockCount = blocks.size();

        request.setAttribute("blocks", blocks);
        request.setAttribute("chainValid", chainValid);
        request.setAttribute("blockCount", blockCount);

        request.getRequestDispatcher("blockchain.jsp").forward(request, response);
    }
}
