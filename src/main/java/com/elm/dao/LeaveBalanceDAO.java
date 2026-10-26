package com.elm.dao;

import com.elm.model.LeaveBalance;
import com.elm.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Data Access Object for leave_balance table.
 */
public class LeaveBalanceDAO {

    /**
     * Retrieves leave balance for a given employee.
     */
    public LeaveBalance getBalanceByEmployeeId(String employeeId) {
        String sql = "SELECT id, employee_id, casual_leave, sick_leave, earned_leave, emergency_leave FROM leave_balance WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractLeaveBalance(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[LeaveBalanceDAO] Error getting balance: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves leave balance within an existing transactional connection.
     */
    public LeaveBalance getBalanceByEmployeeId(String employeeId, Connection conn) throws SQLException {
        String sql = "SELECT id, employee_id, casual_leave, sick_leave, earned_leave, emergency_leave FROM leave_balance WHERE employee_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractLeaveBalance(rs);
                }
            }
        }
        return null;
    }

    /**
     * Returns a HashMap of leave balances for reporting and UI display.
     * Fulfills the HashMap requirement for leave balance mapping.
     */
    public HashMap<String, Integer> getLeaveBalanceMap(String employeeId) {
        HashMap<String, Integer> map = new HashMap<>();
        LeaveBalance balance = getBalanceByEmployeeId(employeeId);
        if (balance != null) {
            map.put("CASUAL", balance.getCasualLeave());
            map.put("SICK", balance.getSickLeave());
            map.put("EARNED", balance.getEarnedLeave());
            map.put("EMERGENCY", balance.getEmergencyLeave());
            map.put("TOTAL_REMAINING", balance.getTotalRemaining());
            map.put("TOTAL_ALLOWED", balance.getTotalAllowed());
            map.put("TOTAL_USED", balance.getTotalUsed());
        } else {
            map.put("CASUAL", 0);
            map.put("SICK", 0);
            map.put("EARNED", 0);
            map.put("EMERGENCY", 0);
            map.put("TOTAL_REMAINING", 0);
            map.put("TOTAL_ALLOWED", 0);
            map.put("TOTAL_USED", 0);
        }
        return map;
    }

    /**
     * Creates initial leave balance row within an existing transaction connection.
     */
    public boolean createLeaveBalance(LeaveBalance balance, Connection conn) throws SQLException {
        String sql = "INSERT INTO leave_balance (employee_id, casual_leave, sick_leave, earned_leave, emergency_leave) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, balance.getEmployeeId());
            ps.setInt(2, balance.getCasualLeave());
            ps.setInt(3, balance.getSickLeave());
            ps.setInt(4, balance.getEarnedLeave());
            ps.setInt(5, balance.getEmergencyLeave());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deducts leave days from the specified leave type in an existing transaction.
     */
    public boolean deductLeaveBalance(String employeeId, String leaveType, int days, Connection conn) throws SQLException {
        if (leaveType == null || days <= 0) {
            return false;
        }

        String column;
        switch (leaveType.toUpperCase().trim()) {
            case "CASUAL":
                column = "casual_leave";
                break;
            case "SICK":
                column = "sick_leave";
                break;
            case "EARNED":
                column = "earned_leave";
                break;
            case "EMERGENCY":
                column = "emergency_leave";
                break;
            default:
                return false;
        }

        // Validate that balance >= days before deducting
        String checkSql = "SELECT " + column + " FROM leave_balance WHERE employee_id = ?";
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, employeeId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    int currentBalance = rs.getInt(1);
                    if (currentBalance < days) {
                        return false; // Insufficient balance
                    }
                } else {
                    return false;
                }
            }
        }

        String sql = "UPDATE leave_balance SET " + column + " = " + column + " - ? WHERE employee_id = ? AND " + column + " >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            ps.setString(2, employeeId);
            ps.setInt(3, days);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes leave balance by employee ID within an existing connection.
     */
    public boolean deleteByEmployeeId(String employeeId, Connection conn) throws SQLException {
        String sql = "DELETE FROM leave_balance WHERE employee_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    private LeaveBalance extractLeaveBalance(ResultSet rs) throws SQLException {
        LeaveBalance balance = new LeaveBalance();
        balance.setId(rs.getInt("id"));
        balance.setEmployeeId(rs.getString("employee_id"));
        balance.setCasualLeave(rs.getInt("casual_leave"));
        balance.setSickLeave(rs.getInt("sick_leave"));
        balance.setEarnedLeave(rs.getInt("earned_leave"));
        balance.setEmergencyLeave(rs.getInt("emergency_leave"));
        return balance;
    }
}
