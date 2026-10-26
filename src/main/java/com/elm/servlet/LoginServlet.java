package com.elm.servlet;

import com.elm.dao.EmployeeDAO;
import com.elm.model.Employee;
import com.elm.model.User;
import com.elm.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles user login requests and session creation.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("role") != null) {
            String role = (String) session.getAttribute("role");
            if ("ADMIN".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            } else if ("EMPLOYEE".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/employee/dashboard");
                return;
            }
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Employee ID / Username and Password are required.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        User user = authService.login(username.trim(), password.trim());

        if (user == null) {
            String dbError = authService.getLastError();
            if (dbError != null) {
                request.setAttribute("errorMessage", "Database Connection Error: Could not connect to MySQL (" + dbError + "). Please ensure MySQL is running on localhost:3306 and database 'employee_leave_db' is imported.");
            } else {
                request.setAttribute("errorMessage", "Invalid Username / Employee ID or Password. Please try again.");
            }
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Authentication successful: create session
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("employeeId", user.getEmployeeId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        // Attach full name for personalized UI greeting
        Employee emp = employeeDAO.getByEmployeeId(user.getEmployeeId());
        if (emp != null) {
            session.setAttribute("fullName", emp.getFullName());
            session.setAttribute("department", emp.getDepartment());
            session.setAttribute("designation", emp.getDesignation());
        } else {
            session.setAttribute("fullName", user.getUsername());
        }

        // Role-based dashboard redirect
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/employee/dashboard");
        }
    }
}
