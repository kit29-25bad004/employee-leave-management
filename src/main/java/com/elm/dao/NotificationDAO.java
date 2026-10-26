package com.elm.dao;

import com.elm.model.Notification;
import com.elm.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Object for notifications table.
 */
public class NotificationDAO {

    /**
     * Creates notification within an existing transaction connection.
     */
    public boolean createNotification(Notification notif, Connection conn) throws SQLException {
        String sql = "INSERT INTO notifications (employee_id, message, is_read) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notif.getEmployeeId());
            ps.setString(2, notif.getMessage());
            ps.setBoolean(3, notif.isRead());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Creates notification with its own connection.
     */
    public boolean createNotification(Notification notif) {
        String sql = "INSERT INTO notifications (employee_id, message, is_read) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notif.getEmployeeId());
            ps.setString(2, notif.getMessage());
            ps.setBoolean(3, notif.isRead());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error creating notification: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all notifications for an employee ordered by date descending.
     * Uses ArrayList as required by assignment specification.
     */
    public ArrayList<Notification> getNotificationsByEmployeeId(String employeeId) {
        ArrayList<Notification> list = new ArrayList<>();
        String sql = "SELECT id, employee_id, message, is_read, created_at FROM notifications WHERE employee_id = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractNotification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error getting notifications: " + e.getMessage());
        }
        return list;
    }

    /**
     * Counts unread notifications for an employee.
     */
    public int getUnreadCount(String employeeId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE employee_id = ? AND is_read = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error getting unread count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Marks a single notification as read.
     */
    public boolean markAsRead(int notificationId, String employeeId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ? AND employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setString(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error marking notification as read: " + e.getMessage());
        }
        return false;
    }

    /**
     * Marks all notifications as read for an employee.
     */
    public boolean markAllAsRead(String employeeId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error marking all notifications as read: " + e.getMessage());
        }
        return false;
    }

    private Notification extractNotification(ResultSet rs) throws SQLException {
        Notification notif = new Notification();
        notif.setId(rs.getInt("id"));
        notif.setEmployeeId(rs.getString("employee_id"));
        notif.setMessage(rs.getString("message"));
        notif.setRead(rs.getBoolean("is_read"));
        notif.setCreatedAt(rs.getTimestamp("created_at"));
        return notif;
    }
}
