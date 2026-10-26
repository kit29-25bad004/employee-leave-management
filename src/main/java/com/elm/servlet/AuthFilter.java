package com.elm.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter providing role-based URL protection and session validation.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI().substring(contextPath.length());

        // Allow static resources
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/") || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }

        // Public URLs
        if (uri.equals("/") || uri.equals("/login") || uri.equals("/login.jsp") || uri.equals("/index.jsp") || uri.equals("/logout")) {
            chain.doFilter(request, response);
            return;
        }

        // Check session
        HttpSession session = httpRequest.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("userId") != null);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (!loggedIn) {
            httpResponse.sendRedirect(contextPath + "/login?error=session_expired");
            return;
        }

        // Admin paths protection
        if (uri.startsWith("/admin/")) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                // Unauthorized employee trying to access admin page
                httpResponse.sendRedirect(contextPath + "/employee/dashboard?error=unauthorized");
                return;
            }
        }

        // Employee paths protection
        if (uri.startsWith("/employee/")) {
            if (!"EMPLOYEE".equalsIgnoreCase(role)) {
                // Admin trying to access employee-specific application forms
                httpResponse.sendRedirect(contextPath + "/admin/dashboard?error=unauthorized");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
