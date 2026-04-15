package com.iomt.filter;

import com.iomt.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AuthFilter - Authentication filter for protected pages
 * Redirects unauthenticated users to the login page
 * Excludes: login, register, CSS, JS, and error pages
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length());

        // Allow public resources without authentication
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is authenticated
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // User is authenticated - continue
        chain.doFilter(request, response);
    }

    /**
     * Check if the requested resource is public (no auth needed)
     */
    private boolean isPublicResource(String path) {
        return path.equals("/login") ||
               path.equals("/register") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.equals("/error.jsp") ||
               path.equals("/") ||
               path.endsWith(".css") ||
               path.endsWith(".js") ||
               path.endsWith(".png") ||
               path.endsWith(".jpg") ||
               path.endsWith(".ico");
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
