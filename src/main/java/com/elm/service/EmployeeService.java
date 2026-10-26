package com.elm.service;

import com.elm.dao.EmployeeDAO;
import com.elm.dao.LeaveBalanceDAO;
import com.elm.dao.NotificationDAO;
import com.elm.dao.UserDAO;
import com.elm.model.Employee;
import com.elm.model.LeaveBalance;
import com.elm.model.Notification;
import com.elm.model.User;
import com.elm.util.DBConnection;
import com.elm.util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Service managing employee operations and business transactions.
 */
public class EmployeeService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final UserDAO userDAO = new UserDAO();
    private final LeaveBalanceDAO balanceDAO = new LeaveBalanceDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    /**
     * Registers a new employee with login credentials and initial leave balance in a single transaction.
     * Rollbacks if any step fails.
     *
     * @param emp           Employee entity
     * @param plainPassword Plain text initial password
     * @return null if successful, or error message string if failed
     */
    public String registerEmployee(Employee emp, String plainPassword) {
        if (emp == null || emp.getEmployeeId() == null || emp.getEmployeeId().trim().isEmpty()) {
            return "Employee ID is required.";
        }
        if (emp.getFullName() == null || emp.getFullName().trim().isEmpty()) {
            return "Full Name is required.";
        }
        if (emp.getEmail() == null || emp.getEmail().trim().isEmpty()) {
            return "Email is required.";
        }
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            return "Password is required.";
        }

        String empId = emp.getEmployeeId().trim().toUpperCase();
        emp.setEmployeeId(empId);

        if (employeeDAO.isEmployeeIdExists(empId)) {
            return "Employee ID '" + empId + "' already exists.";
        }
        if (employeeDAO.isEmailExists(emp.getEmail().trim(), null)) {
            return "Email '" + emp.getEmail().trim() + "' is already registered.";
        }

        String hashedPassword = PasswordUtil.hashPassword(plainPassword.trim());

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Create employee
            boolean empCreated = employeeDAO.createEmployee(emp, conn);
            if (!empCreated) {
                conn.rollback();
                return "Failed to save employee record.";
            }

            // 2. Create user account
            User user = new User(empId, empId, hashedPassword, "EMPLOYEE");
            boolean userCreated = userDAO.createUser(user, conn);
            if (!userCreated) {
                conn.rollback();
                return "Failed to create user credentials.";
            }

            // 3. Create initial leave balance record
            LeaveBalance balance = new LeaveBalance(empId, 12, 10, 15, 5);
            boolean balanceCreated = balanceDAO.createLeaveBalance(balance, conn);
            if (!balanceCreated) {
                conn.rollback();
                return "Failed to initialize leave balance.";
            }

            // 4. Create welcome notification
            Notification notif = new Notification(empId, "Welcome to the Employee Leave Management Portal! Your account has been created.");
            notificationDAO.createNotification(notif, conn);

            conn.commit();
            return null; // Success
        } catch (SQLException e) {
            System.err.println("[EmployeeService] Transaction error creating employee: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("[EmployeeService] Rollback failed: " + ex.getMessage());
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

    public ArrayList<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    public Employee getEmployeeById(String employeeId) {
        return employeeDAO.getByEmployeeId(employeeId);
    }

    public boolean updateEmployee(Employee emp) {
        return employeeDAO.updateEmployee(emp);
    }

    public boolean updateProfile(String employeeId, String fullName, String email, String phone) {
        if (employeeDAO.isEmailExists(email, employeeId)) {
            return false;
        }
        return employeeDAO.updateProfile(employeeId, fullName, email, phone);
    }

    public boolean deleteEmployee(String employeeId) {
        return employeeDAO.deleteEmployee(employeeId);
    }

    public ArrayList<Employee> searchEmployees(String empId, String name, String dept) {
        return employeeDAO.searchEmployees(empId, name, dept);
    }
}
