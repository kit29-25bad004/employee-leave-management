package com.elm.dao;

import com.elm.model.User;
import com.elm.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for users table.
 */
public class UserDAO {

    private String lastError;

    public String getLastError() {
        return lastError;
    }

    /**
     * Authenticates a user by username or email and hashed password.
     */
    public User authenticate(String usernameOrEmail, String hashedPassword) {
        lastError = null;
        String sql = "SELECT u.id, u.employee_id, u.username, u.password, u.role, u.created_at " +
                     "FROM users u " +
                     "LEFT JOIN employees e ON u.employee_id = e.employee_id " +
                     "WHERE (u.username = ? OR e.email = ? OR (? = 'admin@gmail.com' AND u.role = 'ADMIN')) AND u.password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);
            ps.setString(3, usernameOrEmail);
            ps.setString(4, hashedPassword);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.err.println("[UserDAO] Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds a user by their username.
     */
    public User findByUsername(String username) {
        String sql = "SELECT id, employee_id, username, password, role, created_at FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error finding user by username: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds a user by employee ID.
     */
    public User findByEmployeeId(String employeeId) {
        String sql = "SELECT id, employee_id, username, password, role, created_at FROM users WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error finding user by employeeId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Creates a new user record within an existing database transaction connection.
     */
    public boolean createUser(User user, Connection conn) throws SQLException {
        String sql = "INSERT INTO users (employee_id, username, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmployeeId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes user by employee ID within an existing connection.
     */
    public boolean deleteByEmployeeId(String employeeId, Connection conn) throws SQLException {
        String sql = "DELETE FROM users WHERE employee_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmployeeId(rs.getString("employee_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
