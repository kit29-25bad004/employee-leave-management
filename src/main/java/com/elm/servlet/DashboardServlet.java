package com.elm.servlet;

import com.elm.dao.EmployeeDAO;
import com.elm.dao.NotificationDAO;
import com.elm.model.Employee;
import com.elm.model.LeaveApplication;
import com.elm.service.LeaveService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles dashboard rendering for both Employee and Admin portals.
 */
@WebServlet(urlPatterns = {"/employee/dashboard", "/admin/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final LeaveService leaveService = new LeaveService();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=session_expired");
            return;
        }

        String servletPath = request.getServletPath();
        String role = (String) session.getAttribute("role");
        String employeeId = (String) session.getAttribute("employeeId");

        if ("/employee/dashboard".equals(servletPath)) {
            if (!"EMPLOYEE".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            }

            // Employee dashboard data
            Employee employee = employeeDAO.getByEmployeeId(employeeId);
            HashMap<String, Integer> stats = leaveService.getEmployeeLeaveStats(employeeId);
            ArrayList<LeaveApplication> recentLeaves = leaveService.getRecentLeavesByEmployeeId(employeeId, 5);
            int unreadNotifications = notificationDAO.getUnreadCount(employeeId);

            request.setAttribute("employee", employee);
            request.setAttribute("stats", stats);
            request.setAttribute("recentLeaves", recentLeaves);
            request.setAttribute("unreadNotifications", unreadNotifications);

            request.getRequestDispatcher("/employee/dashboard.jsp").forward(request, response);

        } else if ("/admin/dashboard".equals(servletPath)) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/employee/dashboard");
                return;
            }

            // Admin dashboard data
            HashMap<String, Integer> stats = leaveService.getAdminDashboardStats();
            ArrayList<LeaveApplication> recentLeaves = leaveService.getRecentLeavesAdmin(5);
            int pendingCount = leaveService.getPendingLeaves().size();

            request.setAttribute("stats", stats);
            request.setAttribute("recentLeaves", recentLeaves);
            request.setAttribute("pendingCount", pendingCount);

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }
}
