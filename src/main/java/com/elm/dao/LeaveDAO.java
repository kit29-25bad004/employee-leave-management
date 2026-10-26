package com.elm.dao;

import com.elm.model.LeaveApplication;
import com.elm.model.LeaveBalance;
import com.elm.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data Access Object for leave_applications table.
 */
public class LeaveDAO {

    private final LeaveBalanceDAO balanceDAO = new LeaveBalanceDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /**
     * Submits a new leave application in PENDING state.
     */
    public boolean applyLeave(LeaveApplication leave) {
        String sql = "INSERT INTO leave_applications (employee_id, leave_type, from_date, to_date, days, reason, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, leave.getEmployeeId());
            ps.setString(2, leave.getLeaveType());
            ps.setDate(3, leave.getFromDate());
            ps.setDate(4, leave.getToDate());
            ps.setInt(5, leave.getDays());
            ps.setString(6, leave.getReason());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        leave.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error applying for leave: " + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if the employee has any overlapping pending or approved leave in the given date range.
     */
    public boolean hasOverlappingLeave(String employeeId, Date fromDate, Date toDate) {
        String sql = "SELECT 1 FROM leave_applications " +
                     "WHERE employee_id = ? AND status != 'REJECTED' " +
                     "AND from_date <= ? AND to_date >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.setDate(2, toDate);   // overlap condition: existing.from_date <= request.to_date
            ps.setDate(3, fromDate); // AND existing.to_date >= request.from_date
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error checking overlapping leaves: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all leave applications for an employee.
     * Uses ArrayList as required by assignment specification.
     */
    public ArrayList<LeaveApplication> getLeavesByEmployeeId(String employeeId) {
        ArrayList<LeaveApplication> list = new ArrayList<>();
        String sql = "SELECT l.*, e.full_name, e.department FROM leave_applications l " +
                     "JOIN employees e ON l.employee_id = e.employee_id " +
                     "WHERE l.employee_id = ? ORDER BY l.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractLeave(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error getting employee leaves: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves recent leave applications for an employee with limit.
     */
    public ArrayList<LeaveApplication> getRecentLeavesByEmployeeId(String employeeId, int limit) {
        ArrayList<LeaveApplication> list = new ArrayList<>();
        String sql = "SELECT l.*, e.full_name, e.department FROM leave_applications l " +
                     "JOIN employees e ON l.employee_id = e.employee_id " +
                     "WHERE l.employee_id = ? ORDER BY l.id DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractLeave(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error getting recent leaves: " + e.getMessage());
        }
        return list;
    }

    /**
     * Filters leave applications for an employee based on status, type, dates, and search keyword.
     */
    public ArrayList<LeaveApplication> filterEmployeeLeaves(String employeeId, String status, 
                                                            String leaveType, Date fromDate, Date toDate, String search) {
        ArrayList<LeaveApplication> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT l.*, e.full_name, e.department FROM leave_applications l " +
            "JOIN employees e ON l.employee_id = e.employee_id " +
            "WHERE l.employee_id = ?"
        );
        ArrayList<Object> params = new ArrayList<>();
        params.add(employeeId);

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            sql.append(" AND l.status = ?");
            params.add(status.trim().toUpperCase());
        }
        if (leaveType != null && !leaveType.trim().isEmpty() && !leaveType.equalsIgnoreCase("ALL")) {
            sql.append(" AND l.leave_type = ?");
            params.add(leaveType.trim().toUpperCase());
        }
        if (fromDate != null) {
            sql.append(" AND l.from_date >= ?");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append(" AND l.to_date <= ?");
            params.add(toDate);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (l.reason LIKE ? OR CAST(l.id AS CHAR) LIKE ?)");
            String term = "%" + search.trim() + "%";
            params.add(term);
            params.add(term);
        }

        sql.append(" ORDER BY l.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String s) {
                    ps.setString(i + 1, s);
                } else if (p instanceof Date d) {
                    ps.setDate(i + 1, d);
                } else if (p instanceof Integer integer) {
                    ps.setInt(i + 1, integer);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractLeave(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error filtering employee leaves: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves all leave applications across all employees.
     */
    public ArrayList<LeaveApplication> getAllLeaves() {
        ArrayList<LeaveApplication> list = new ArrayList<>();
        String sql = "SELECT l.*, e.full_name, e.department FROM leave_applications l " +
                     "JOIN employees e ON l.employee_id = e.employee_id ORDER BY l.id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractLeave(rs));
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error getting all leaves: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves recent leave applications across all employees for Admin dashboard.
     */
    public ArrayList<LeaveApplication> getRecentLeavesAdmin(int limit) {
        ArrayList<LeaveApplication> list = new ArrayList<>();
        String sql = "SELECT l.*, e.full_name, e.department FROM leave_applications l " +
                     "JOIN employees e ON l.employee_id = e.employee_id ORDER BY l.id DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractLeave(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error getting recent admin leaves: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves all PENDING leave applications for Admin approval.
     */
    public ArrayList<LeaveApplication> getPendingLeaves() {
        ArrayList<LeaveApplication> list = new ArrayList<>();
        String sql = "SELECT l.*, e.full_name, e.department FROM leave_applications l " +
                     "JOIN employees e ON l.employee_id = e.employee_id " +
                     "WHERE l.status = 'PENDING' ORDER BY l.applied_at ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractLeave(rs));
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error getting pending leaves: " + e.getMessage());
        }
        return list;
    }

    /**
     * Filters all leave applications with multi-criteria for Admin Leave History.
     */
    public ArrayList<LeaveApplication> filterAllLeaves(String employeeSearch, String department, 
                                                       String leaveType, String status, Date fromDate, Date toDate) {
        ArrayList<LeaveApplication> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT l.*, e.full_name, e.department FROM leave_applications l " +
            "JOIN employees e ON l.employee_id = e.employee_id WHERE 1=1"
        );
        ArrayList<Object> params = new ArrayList<>();

        if (employeeSearch != null && !employeeSearch.trim().isEmpty()) {
            sql.append(" AND (l.employee_id LIKE ? OR e.full_name LIKE ?)");
            String term = "%" + employeeSearch.trim() + "%";
            params.add(term);
            params.add(term);
        }
        if (department != null && !department.trim().isEmpty() && !department.equalsIgnoreCase("ALL")) {
            sql.append(" AND e.department = ?");
            params.add(department.trim());
        }
        if (leaveType != null && !leaveType.trim().isEmpty() && !leaveType.equalsIgnoreCase("ALL")) {
            sql.append(" AND l.leave_type = ?");
            params.add(leaveType.trim().toUpperCase());
        }
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            sql.append(" AND l.status = ?");
            params.add(status.trim().toUpperCase());
        }
        if (fromDate != null) {
            sql.append(" AND l.from_date >= ?");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append(" AND l.to_date <= ?");
            params.add(toDate);
        }

        sql.append(" ORDER BY l.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String s) {
                    ps.setString(i + 1, s);
                } else if (p instanceof Date d) {
                    ps.setDate(i + 1, d);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractLeave(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error filtering admin leaves: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves a leave application by ID.
     */
    public LeaveApplication getLeaveById(int id) {
        String sql = "SELECT l.*, e.full_name, e.department FROM leave_applications l " +
                     "JOIN employees e ON l.employee_id = e.employee_id WHERE l.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractLeave(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error getting leave by id: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a leave application by ID within an existing connection.
     */
    public LeaveApplication getLeaveById(int id, Connection conn) throws SQLException {
        String sql = "SELECT l.*, e.full_name, e.department FROM leave_applications l " +
                     "JOIN employees e ON l.employee_id = e.employee_id WHERE l.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractLeave(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates status of leave application within an existing transaction connection.
     */
    public boolean updateLeaveStatus(int leaveId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE leave_applications SET status = ?, approved_at = " +
                     ("APPROVED".equalsIgnoreCase(status) ? "NOW()" : "NULL") +
                     " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.toUpperCase());
            ps.setInt(2, leaveId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Computes leave statistics for an individual employee.
     * Fulfills the HashMap requirement.
     */
    public HashMap<String, Integer> getEmployeeLeaveStats(String employeeId) {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("TOTAL_LEAVES", 0);
        stats.put("APPROVED_LEAVES", 0);
        stats.put("PENDING_LEAVES", 0);
        stats.put("REJECTED_LEAVES", 0);
        stats.put("REMAINING_LEAVES", 0);
        stats.put("USED_LEAVES", 0);

        String sql = "SELECT status, COUNT(*) AS cnt, SUM(days) AS total_days " +
                     "FROM leave_applications WHERE employee_id = ? GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                int totalCount = 0;
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("cnt");
                    totalCount += count;
                    if ("APPROVED".equalsIgnoreCase(status)) {
                        stats.put("APPROVED_LEAVES", count);
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        stats.put("PENDING_LEAVES", count);
                    } else if ("REJECTED".equalsIgnoreCase(status)) {
                        stats.put("REJECTED_LEAVES", count);
                    }
                }
                stats.put("TOTAL_LEAVES", totalCount);
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error getting employee leave stats: " + e.getMessage());
        }

        // Remaining and used leaves from leave_balance table
        LeaveBalance balance = balanceDAO.getBalanceByEmployeeId(employeeId);
        if (balance != null) {
            stats.put("REMAINING_LEAVES", balance.getTotalRemaining());
            stats.put("USED_LEAVES", balance.getTotalUsed());
        }

        return stats;
    }

    /**
     * Computes overall dashboard statistics for Admin.
     * Fulfills the HashMap requirement.
     */
    public HashMap<String, Integer> getAdminDashboardStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("TOTAL_EMPLOYEES", employeeDAO.getTotalEmployeeCount());
        stats.put("TOTAL_REQUESTS", 0);
        stats.put("PENDING_REQUESTS", 0);
        stats.put("APPROVED_REQUESTS", 0);
        stats.put("REJECTED_REQUESTS", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM leave_applications GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int total = 0;
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("cnt");
                total += count;
                if ("PENDING".equalsIgnoreCase(status)) {
                    stats.put("PENDING_REQUESTS", count);
                } else if ("APPROVED".equalsIgnoreCase(status)) {
                    stats.put("APPROVED_REQUESTS", count);
                } else if ("REJECTED".equalsIgnoreCase(status)) {
                    stats.put("REJECTED_REQUESTS", count);
                }
            }
            stats.put("TOTAL_REQUESTS", total);
        } catch (SQLException e) {
            System.err.println("[LeaveDAO] Error getting admin stats: " + e.getMessage());
        }

        return stats;
    }

    private LeaveApplication extractLeave(ResultSet rs) throws SQLException {
        LeaveApplication leave = new LeaveApplication();
        leave.setId(rs.getInt("id"));
        leave.setEmployeeId(rs.getString("employee_id"));
        leave.setLeaveType(rs.getString("leave_type"));
        leave.setFromDate(rs.getDate("from_date"));
        leave.setToDate(rs.getDate("to_date"));
        leave.setDays(rs.getInt("days"));
        leave.setReason(rs.getString("reason"));
        leave.setStatus(rs.getString("status"));
        leave.setAppliedAt(rs.getTimestamp("applied_at"));
        leave.setApprovedAt(rs.getTimestamp("approved_at"));

        try {
            leave.setEmployeeName(rs.getString("full_name"));
        } catch (SQLException ignored) {}

        try {
            leave.setDepartment(rs.getString("department"));
        } catch (SQLException ignored) {}

        return leave;
    }
}
