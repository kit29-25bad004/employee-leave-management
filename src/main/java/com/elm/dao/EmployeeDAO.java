package com.elm.dao;

import com.elm.model.Employee;
import com.elm.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Data Access Object for employees table.
 */
public class EmployeeDAO {

    /**
     * Retrieves all employees ordered by creation date descending.
     * Uses ArrayList as required by assignment specification.
     */
    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> list = new ArrayList<>();
        String sql = "SELECT id, employee_id, full_name, email, phone, department, designation, date_of_joining, created_at " +
                     "FROM employees ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractEmployee(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EmployeeDAO] Error getting all employees: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves an employee by their unique employee_id.
     */
    public Employee getByEmployeeId(String employeeId) {
        String sql = "SELECT id, employee_id, full_name, email, phone, department, designation, date_of_joining, created_at " +
                     "FROM employees WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[EmployeeDAO] Error getting employee by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if employee_id already exists.
     */
    public boolean isEmployeeIdExists(String employeeId) {
        String sql = "SELECT 1 FROM employees WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[EmployeeDAO] Error checking employee ID: " + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if email already exists, optionally excluding a specific employee ID.
     */
    public boolean isEmailExists(String email, String excludeEmployeeId) {
        String sql = "SELECT 1 FROM employees WHERE email = ?" + 
                     (excludeEmployeeId != null ? " AND employee_id != ?" : "");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            if (excludeEmployeeId != null) {
                ps.setString(2, excludeEmployeeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[EmployeeDAO] Error checking email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Inserts an employee within an existing transaction connection.
     */
    public boolean createEmployee(Employee emp, Connection conn) throws SQLException {
        String sql = "INSERT INTO employees (employee_id, full_name, email, phone, department, designation, date_of_joining) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getEmployeeId());
            ps.setString(2, emp.getFullName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getDepartment());
            ps.setString(6, emp.getDesignation());
            ps.setDate(7, emp.getDateOfJoining());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates an employee's details (admin edit).
     */
    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET full_name = ?, email = ?, phone = ?, department = ?, designation = ?, date_of_joining = ? " +
                     "WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getFullName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone());
            ps.setString(4, emp.getDepartment());
            ps.setString(5, emp.getDesignation());
            ps.setDate(6, emp.getDateOfJoining());
            ps.setString(7, emp.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EmployeeDAO] Error updating employee: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates employee profile details (allowed for employee self-service).
     */
    public boolean updateProfile(String employeeId, String fullName, String email, String phone) {
        String sql = "UPDATE employees SET full_name = ?, email = ?, phone = ? WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EmployeeDAO] Error updating profile: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes an employee along with related records inside an atomic transaction.
     */
    public boolean deleteEmployee(String employeeId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete notifications
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notifications WHERE employee_id = ?")) {
                ps.setString(1, employeeId);
                ps.executeUpdate();
            }

            // Delete leave applications
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM leave_applications WHERE employee_id = ?")) {
                ps.setString(1, employeeId);
                ps.executeUpdate();
            }

            // Delete leave balance
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM leave_balance WHERE employee_id = ?")) {
                ps.setString(1, employeeId);
                ps.executeUpdate();
            }

            // Delete user login
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE employee_id = ?")) {
                ps.setString(1, employeeId);
                ps.executeUpdate();
            }

            // Delete employee record
            boolean deleted;
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM employees WHERE employee_id = ?")) {
                ps.setString(1, employeeId);
                deleted = ps.executeUpdate() > 0;
            }

            if (deleted) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[EmployeeDAO] Error deleting employee: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("[EmployeeDAO] Rollback failed: " + ex.getMessage());
                }
            }
            return false;
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
     * Searches employees by employee ID, Name, and Department.
     * Uses ArrayList as required by assignment specification.
     */
    public ArrayList<Employee> searchEmployees(String empIdQuery, String nameQuery, String deptQuery) {
        ArrayList<Employee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT id, employee_id, full_name, email, phone, department, designation, date_of_joining, created_at " +
            "FROM employees WHERE 1=1"
        );
        ArrayList<String> params = new ArrayList<>();

        if (empIdQuery != null && !empIdQuery.trim().isEmpty()) {
            sql.append(" AND employee_id LIKE ?");
            params.add("%" + empIdQuery.trim() + "%");
        }
        if (nameQuery != null && !nameQuery.trim().isEmpty()) {
            sql.append(" AND full_name LIKE ?");
            params.add("%" + nameQuery.trim() + "%");
        }
        if (deptQuery != null && !deptQuery.trim().isEmpty()) {
            sql.append(" AND department LIKE ?");
            params.add("%" + deptQuery.trim() + "%");
        }

        sql.append(" ORDER BY id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractEmployee(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[EmployeeDAO] Error searching employees: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns total count of registered employees.
     */
    public int getTotalEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM employees";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[EmployeeDAO] Error getting employee count: " + e.getMessage());
        }
        return 0;
    }

    private Employee extractEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setId(rs.getInt("id"));
        emp.setEmployeeId(rs.getString("employee_id"));
        emp.setFullName(rs.getString("full_name"));
        emp.setEmail(rs.getString("email"));
        emp.setPhone(rs.getString("phone"));
        emp.setDepartment(rs.getString("department"));
        emp.setDesignation(rs.getString("designation"));
        emp.setDateOfJoining(rs.getDate("date_of_joining"));
        emp.setCreatedAt(rs.getTimestamp("created_at"));
        return emp;
    }
}
