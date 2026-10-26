package com.elm.servlet;

import com.elm.model.LeaveApplication;
import com.elm.model.LeaveBalance;
import com.elm.service.LeaveService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles employee leave application, leave history viewing with filters, and leave balance display.
 */
@WebServlet(urlPatterns = {"/employee/apply-leave", "/employee/leave-history", "/employee/leave-balance"})
public class LeaveServlet extends HttpServlet {

    private final LeaveService leaveService = new LeaveService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String employeeId = (String) session.getAttribute("employeeId");
        String path = request.getServletPath();

        switch (path) {
            case "/employee/apply-leave":
                handleApplyLeaveGet(request, response, employeeId);
                break;
            case "/employee/leave-history":
                handleLeaveHistoryGet(request, response, employeeId);
                break;
            case "/employee/leave-balance":
                handleLeaveBalanceGet(request, response, employeeId);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/employee/dashboard");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String employeeId = (String) session.getAttribute("employeeId");
        String path = request.getServletPath();

        if ("/employee/apply-leave".equals(path)) {
            handleApplyLeavePost(request, response, employeeId);
        } else {
            doGet(request, response);
        }
    }

    private void handleApplyLeaveGet(HttpServletRequest request, HttpServletResponse response, String employeeId)
            throws ServletException, IOException {
        LeaveBalance balance = leaveService.getLeaveBalance(employeeId);
        HashMap<String, Integer> balanceMap = leaveService.getLeaveBalanceMap(employeeId);

        request.setAttribute("balance", balance);
        request.setAttribute("balanceMap", balanceMap);
        request.getRequestDispatcher("/employee/apply-leave.jsp").forward(request, response);
    }

    private void handleApplyLeavePost(HttpServletRequest request, HttpServletResponse response, String employeeId)
            throws ServletException, IOException {
        String leaveType = request.getParameter("leaveType");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");
        String reason = request.getParameter("reason");

        // Basic parameter check
        if (leaveType == null || leaveType.trim().isEmpty() ||
            fromDateStr == null || fromDateStr.trim().isEmpty() ||
            toDateStr == null || toDateStr.trim().isEmpty() ||
            reason == null || reason.trim().isEmpty()) {

            request.setAttribute("errorMessage", "All fields are required. Please fill in all details.");
            handleApplyLeaveGet(request, response, employeeId);
            return;
        }

        try {
            LocalDate fromLocal = LocalDate.parse(fromDateStr.trim());
            LocalDate toLocal = LocalDate.parse(toDateStr.trim());

            if (fromLocal.isAfter(toLocal)) {
                request.setAttribute("errorMessage", "From Date cannot be after To Date.");
                handleApplyLeaveGet(request, response, employeeId);
                return;
            }

            // Calculate number of inclusive days on the server
            int serverCalculatedDays = (int) ChronoUnit.DAYS.between(fromLocal, toLocal) + 1;
            if (serverCalculatedDays <= 0) {
                request.setAttribute("errorMessage", "Invalid duration. Days must be greater than zero.");
                handleApplyLeaveGet(request, response, employeeId);
                return;
            }

            Date fromDate = Date.valueOf(fromLocal);
            Date toDate = Date.valueOf(toLocal);

            LeaveApplication leave = new LeaveApplication(
                employeeId,
                leaveType.trim().toUpperCase(),
                fromDate,
                toDate,
                serverCalculatedDays,
                reason.trim()
            );

            // Execute application with service validations (overlap, balance)
            String error = leaveService.applyLeave(leave);

            if (error != null) {
                request.setAttribute("errorMessage", error);
                handleApplyLeaveGet(request, response, employeeId);
            } else {
                response.sendRedirect(request.getContextPath() + "/employee/leave-history?success=applied");
            }

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Invalid date format submitted: " + e.getMessage());
            handleApplyLeaveGet(request, response, employeeId);
        }
    }

    private void handleLeaveHistoryGet(HttpServletRequest request, HttpServletResponse response, String employeeId)
            throws ServletException, IOException {
        String status = request.getParameter("status");
        String leaveType = request.getParameter("leaveType");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");
        String search = request.getParameter("search");

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

        ArrayList<LeaveApplication> leaves = leaveService.filterEmployeeLeaves(
            employeeId, status, leaveType, fromDate, toDate, search
        );

        request.setAttribute("leaves", leaves);
        request.setAttribute("statusFilter", status);
        request.setAttribute("leaveTypeFilter", leaveType);
        request.setAttribute("fromDateFilter", fromDateStr);
        request.setAttribute("toDateFilter", toDateStr);
        request.setAttribute("searchFilter", search);

        request.getRequestDispatcher("/employee/leave-history.jsp").forward(request, response);
    }

    private void handleLeaveBalanceGet(HttpServletRequest request, HttpServletResponse response, String employeeId)
            throws ServletException, IOException {
        LeaveBalance balance = leaveService.getLeaveBalance(employeeId);
        HashMap<String, Integer> balanceMap = leaveService.getLeaveBalanceMap(employeeId);

        request.setAttribute("balance", balance);
        request.setAttribute("balanceMap", balanceMap);
        request.getRequestDispatcher("/employee/leave-balance.jsp").forward(request, response);
    }
}
