package com.elm.servlet;

import com.elm.model.LeaveApplication;
import com.elm.service.LeaveService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Handles admin leave approvals, rejections, pending queue, and overall history filters.
 */
@WebServlet(urlPatterns = {"/admin/leave-requests", "/admin/leave-action", "/admin/leave-history"})
public class AdminLeaveServlet extends HttpServlet {

    private final LeaveService leaveService = new LeaveService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/leave-requests":
                handleLeaveRequestsGet(request, response);
                break;
            case "/admin/leave-history":
                handleLeaveHistoryGet(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/admin/leave-action".equals(path)) {
            handleLeaveActionPost(request, response);
        } else {
            doGet(request, response);
        }
    }

    private void handleLeaveRequestsGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<LeaveApplication> pendingLeaves = leaveService.getPendingLeaves();
        request.setAttribute("pendingLeaves", pendingLeaves);
        request.getRequestDispatcher("/admin/leave-requests.jsp").forward(request, response);
    }

    private void handleLeaveActionPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String leaveIdStr = request.getParameter("leaveId");
        String action = request.getParameter("action");

        if (leaveIdStr == null || action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/leave-requests?error=invalid_request");
            return;
        }

        try {
            int leaveId = Integer.parseInt(leaveIdStr.trim());
            String errorMsg = null;

            if ("APPROVE".equalsIgnoreCase(action)) {
                errorMsg = leaveService.approveLeave(leaveId);
            } else if ("REJECT".equalsIgnoreCase(action)) {
                errorMsg = leaveService.rejectLeave(leaveId);
            } else {
                errorMsg = "Unknown action requested.";
            }

            if (errorMsg != null) {
                response.sendRedirect(request.getContextPath() + "/admin/leave-requests?error=" + 
                                      java.net.URLEncoder.encode(errorMsg, java.nio.charset.StandardCharsets.UTF_8));
            } else {
                String successMsg = "APPROVE".equalsIgnoreCase(action) ? "Leave approved successfully!" : "Leave rejected.";
                response.sendRedirect(request.getContextPath() + "/admin/leave-requests?msg=" + 
                                      java.net.URLEncoder.encode(successMsg, java.nio.charset.StandardCharsets.UTF_8));
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/leave-requests?error=invalid_id");
        }
    }

    private void handleLeaveHistoryGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String employeeSearch = request.getParameter("employeeSearch");
        String department = request.getParameter("department");
        String leaveType = request.getParameter("leaveType");
        String status = request.getParameter("status");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");

        Date fromDate = null;
        Date toDate = null;

        if (fromDateStr != null && !fromDateStr.trim().isEmpty()) {
            try {
                fromDate = Date.valueOf(fromDateStr.trim());
            } catch (Exception ignored) {}
        }
        if (toDateStr != null && !toDateStr.trim().isEmpty()) {
            try {
                toDate = Date.valueOf(toDateStr.trim());
            } catch (Exception ignored) {}
        }

        ArrayList<LeaveApplication> leaves = leaveService.filterAllLeaves(
            employeeSearch, department, leaveType, status, fromDate, toDate
        );

        request.setAttribute("leaves", leaves);
        request.setAttribute("employeeSearch", employeeSearch);
        request.setAttribute("department", department);
        request.setAttribute("leaveType", leaveType);
        request.setAttribute("status", status);
        request.setAttribute("fromDate", fromDateStr);
        request.setAttribute("toDate", toDateStr);

        request.getRequestDispatcher("/admin/leave-history.jsp").forward(request, response);
    }
}
