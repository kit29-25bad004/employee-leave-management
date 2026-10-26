package com.elm.service;

import com.elm.dao.LeaveBalanceDAO;
import com.elm.dao.LeaveDAO;
import com.elm.dao.NotificationDAO;
import com.elm.model.LeaveApplication;
import com.elm.model.LeaveBalance;
import com.elm.model.Notification;
import com.elm.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Service managing leave business workflows, validations, and transactions.
 */
public class LeaveService {

    private final LeaveDAO leaveDAO = new LeaveDAO();
    private final LeaveBalanceDAO balanceDAO = new LeaveBalanceDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    /**
     * Validates and applies for leave.
     * Returns null on success, or user-friendly error message on validation failure.
     */
    public String applyLeave(LeaveApplication leave) {
        if (leave == null) {
            return "Invalid leave application data.";
        }
        if (leave.getEmployeeId() == null || leave.getEmployeeId().trim().isEmpty()) {
            return "Session expired or invalid employee identity.";
        }
        if (leave.getLeaveType() == null || leave.getLeaveType().trim().isEmpty()) {
            return "Please select a leave type.";
        }
        if (leave.getFromDate() == null || leave.getToDate() == null) {
            return "Please provide both From Date and To Date.";
        }
        if (leave.getFromDate().after(leave.getToDate())) {
            return "From Date cannot be after To Date.";
        }
        if (leave.getDays() <= 0) {
            return "Number of leave days must be greater than zero.";
        }
        if (leave.getReason() == null || leave.getReason().trim().isEmpty()) {
            return "Please provide a valid reason for leave.";
        }

        // Validate available leave balance
        LeaveBalance balance = balanceDAO.getBalanceByEmployeeId(leave.getEmployeeId());
        if (balance == null) {
            return "Leave balance record not found for employee.";
        }
        int available = balance.getBalanceForType(leave.getLeaveType());
        if (available < leave.getDays()) {
            return "Insufficient " + leave.getLeaveType() + " leave balance! You have " + 
                   available + " days remaining, but requested " + leave.getDays() + " days.";
        }

        // Check for overlapping leaves
        if (leaveDAO.hasOverlappingLeave(leave.getEmployeeId(), leave.getFromDate(), leave.getToDate())) {
            return "You already have an existing pending or approved leave request during this date range (" +
                   leave.getFromDate() + " to " + leave.getToDate() + ").";
        }

        // Insert leave record
        boolean saved = leaveDAO.applyLeave(leave);
        if (saved) {
            // Notify employee of submission
            Notification notif = new Notification(
                leave.getEmployeeId(),
                "Your " + leave.getLeaveType() + " leave request for " + leave.getDays() + 
                " day(s) from " + leave.getFromDate() + " to " + leave.getToDate() + 
                " was submitted and is currently PENDING review."
            );
            notificationDAO.createNotification(notif);
            return null; // Success
        }

        return "Failed to submit leave application due to a database error.";
    }

    /**
     * Approves leave inside an atomic transaction:
     * 1. Checks status is PENDING
     * 2. Deducts leave balance
     * 3. Changes status to APPROVED
     * 4. Creates notification
     * 5. Commits transaction (rollback on any failure)
     */
    public String approveLeave(int leaveId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            LeaveApplication leave = leaveDAO.getLeaveById(leaveId, conn);
            if (leave == null) {
                conn.rollback();
                return "Leave application not found.";
            }

            if (!leave.isPending()) {
                conn.rollback();
                return "This leave application has already been processed (Current status: " + leave.getStatus() + ").";
            }

            // Deduct balance
            boolean deducted = balanceDAO.deductLeaveBalance(leave.getEmployeeId(), leave.getLeaveType(), leave.getDays(), conn);
            if (!deducted) {
                conn.rollback();
                return "Cannot approve leave: Employee has insufficient " + leave.getLeaveType() + " balance.";
            }

            // Update status
            boolean updated = leaveDAO.updateLeaveStatus(leaveId, "APPROVED", conn);
            if (!updated) {
                conn.rollback();
                return "Failed to update leave status.";
            }

            // Create notification
            Notification notif = new Notification(
                leave.getEmployeeId(),
                "Great news! Your " + leave.getLeaveType() + " leave application (#" + leave.getId() + 
                ") for " + leave.getDays() + " day(s) has been APPROVED by HR."
            );
            notificationDAO.createNotification(notif, conn);

            conn.commit();
            return null; // Success
        } catch (SQLException e) {
            System.err.println("[LeaveService] Transaction error approving leave: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("[LeaveService] Rollback failed: " + ex.getMessage());
                }
            }
            return "Database transaction error: " + e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Rejects leave inside an atomic transaction:
     * 1. Checks status is PENDING
     * 2. Changes status to REJECTED (no balance deduction)
     * 3. Creates notification
     * 4. Commits transaction
     */
    public String rejectLeave(int leaveId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            LeaveApplication leave = leaveDAO.getLeaveById(leaveId, conn);
            if (leave == null) {
                conn.rollback();
                return "Leave application not found.";
            }

            if (!leave.isPending()) {
                conn.rollback();
                return "This leave application has already been processed (Current status: " + leave.getStatus() + ").";
            }

            // Update status to REJECTED
            boolean updated = leaveDAO.updateLeaveStatus(leaveId, "REJECTED", conn);
            if (!updated) {
                conn.rollback();
                return "Failed to update leave status.";
            }

            // Create notification
            Notification notif = new Notification(
                leave.getEmployeeId(),
                "Your " + leave.getLeaveType() + " leave application (#" + leave.getId() + 
                ") for " + leave.getDays() + " day(s) has been REJECTED by HR."
            );
            notificationDAO.createNotification(notif, conn);

            conn.commit();
            return null; // Success
        } catch (SQLException e) {
            System.err.println("[LeaveService] Transaction error rejecting leave: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("[LeaveService] Rollback failed: " + ex.getMessage());
                }
            }
            return "Database transaction error: " + e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public ArrayList<LeaveApplication> getLeavesByEmployeeId(String employeeId) {
        return leaveDAO.getLeavesByEmployeeId(employeeId);
    }

    public ArrayList<LeaveApplication> getRecentLeavesByEmployeeId(String employeeId, int limit) {
        return leaveDAO.getRecentLeavesByEmployeeId(employeeId, limit);
    }

    public ArrayList<LeaveApplication> filterEmployeeLeaves(String employeeId, String status, 
                                                            String leaveType, Date fromDate, Date toDate, String search) {
        return leaveDAO.filterEmployeeLeaves(employeeId, status, leaveType, fromDate, toDate, search);
    }

    public ArrayList<LeaveApplication> getAllLeaves() {
        return leaveDAO.getAllLeaves();
    }

    public ArrayList<LeaveApplication> getRecentLeavesAdmin(int limit) {
        return leaveDAO.getRecentLeavesAdmin(limit);
    }

    public ArrayList<LeaveApplication> getPendingLeaves() {
        return leaveDAO.getPendingLeaves();
    }

    public ArrayList<LeaveApplication> filterAllLeaves(String employeeSearch, String department, 
                                                       String leaveType, String status, Date fromDate, Date toDate) {
        return leaveDAO.filterAllLeaves(employeeSearch, department, leaveType, status, fromDate, toDate);
    }

    public LeaveBalance getLeaveBalance(String employeeId) {
        return balanceDAO.getBalanceByEmployeeId(employeeId);
    }

    public HashMap<String, Integer> getLeaveBalanceMap(String employeeId) {
        return balanceDAO.getLeaveBalanceMap(employeeId);
    }

    public HashMap<String, Integer> getEmployeeLeaveStats(String employeeId) {
        return leaveDAO.getEmployeeLeaveStats(employeeId);
    }

    public HashMap<String, Integer> getAdminDashboardStats() {
        return leaveDAO.getAdminDashboardStats();
    }
}
