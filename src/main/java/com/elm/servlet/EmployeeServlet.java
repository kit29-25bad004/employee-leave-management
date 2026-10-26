package com.elm.servlet;

import com.elm.model.Employee;
import com.elm.service.EmployeeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Handles employee management operations (CRUD and search) for administrators.
 */
@WebServlet(urlPatterns = {"/admin/employees", "/admin/add-employee", "/admin/edit-employee", "/admin/delete-employee"})
public class EmployeeServlet extends HttpServlet {

    private final EmployeeService employeeService = new EmployeeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/employees":
                handleListEmployees(request, response);
                break;
            case "/admin/add-employee":
                request.getRequestDispatcher("/admin/add-employee.jsp").forward(request, response);
                break;
            case "/admin/edit-employee":
                handleEditEmployeeGet(request, response);
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

        switch (path) {
            case "/admin/add-employee":
                handleAddEmployeePost(request, response);
                break;
            case "/admin/edit-employee":
                handleEditEmployeePost(request, response);
                break;
            case "/admin/delete-employee":
                handleDeleteEmployeePost(request, response);
                break;
            default:
                doGet(request, response);
                break;
        }
    }

    private void handleListEmployees(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String empIdQuery = request.getParameter("employeeId");
        String nameQuery = request.getParameter("name");
        String deptQuery = request.getParameter("department");

        ArrayList<Employee> employees = employeeService.searchEmployees(empIdQuery, nameQuery, deptQuery);

        request.setAttribute("employees", employees);
        request.setAttribute("searchEmpId", empIdQuery);
        request.setAttribute("searchName", nameQuery);
        request.setAttribute("searchDept", deptQuery);

        request.getRequestDispatcher("/admin/employees.jsp").forward(request, response);
    }

    private void handleAddEmployeePost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String employeeId = request.getParameter("employeeId");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String department = request.getParameter("department");
        String designation = request.getParameter("designation");
        String dateOfJoiningStr = request.getParameter("dateOfJoining");
        String password = request.getParameter("password");

        if (employeeId == null || employeeId.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            department == null || department.trim().isEmpty() ||
            designation == null || designation.trim().isEmpty() ||
            dateOfJoiningStr == null || dateOfJoiningStr.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {

            request.setAttribute("errorMessage", "All fields including password are required.");
            request.setAttribute("formData", request.getParameterMap());
            request.getRequestDispatcher("/admin/add-employee.jsp").forward(request, response);
            return;
        }

        try {
            Date dateOfJoining = Date.valueOf(dateOfJoiningStr.trim());
            Employee emp = new Employee(
                employeeId.trim().toUpperCase(),
                fullName.trim(),
                email.trim(),
                phone.trim(),
                department.trim(),
                designation.trim(),
                dateOfJoining
            );

            String errorMsg = employeeService.registerEmployee(emp, password.trim());
            if (errorMsg != null) {
                request.setAttribute("errorMessage", errorMsg);
                request.getRequestDispatcher("/admin/add-employee.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/employees?msg=" + 
                                      URLEncoder.encode("Employee " + emp.getEmployeeId() + " created successfully!", StandardCharsets.UTF_8));
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid Date of Joining format.");
            request.getRequestDispatcher("/admin/add-employee.jsp").forward(request, response);
        }
    }

    private void handleEditEmployeeGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String empId = request.getParameter("employeeId");
        if (empId == null || empId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/employees");
            return;
        }

        Employee emp = employeeService.getEmployeeById(empId.trim());
        if (emp == null) {
            response.sendRedirect(request.getContextPath() + "/admin/employees?error=not_found");
            return;
        }

        request.setAttribute("employee", emp);
        request.getRequestDispatcher("/admin/edit-employee.jsp").forward(request, response);
    }

    private void handleEditEmployeePost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String employeeId = request.getParameter("employeeId");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String department = request.getParameter("department");
        String designation = request.getParameter("designation");
        String dateOfJoiningStr = request.getParameter("dateOfJoining");

        if (employeeId == null || employeeId.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            department == null || department.trim().isEmpty() ||
            designation == null || designation.trim().isEmpty() ||
            dateOfJoiningStr == null || dateOfJoiningStr.trim().isEmpty()) {

            request.setAttribute("errorMessage", "All fields are required.");
            Employee emp = employeeService.getEmployeeById(employeeId);
            request.setAttribute("employee", emp);
            request.getRequestDispatcher("/admin/edit-employee.jsp").forward(request, response);
            return;
        }

        try {
            Date dateOfJoining = Date.valueOf(dateOfJoiningStr.trim());
            Employee emp = new Employee(
                employeeId.trim(),
                fullName.trim(),
                email.trim(),
                phone.trim(),
                department.trim(),
                designation.trim(),
                dateOfJoining
            );

            boolean updated = employeeService.updateEmployee(emp);
            if (updated) {
                response.sendRedirect(request.getContextPath() + "/admin/employees?msg=" + 
                                      URLEncoder.encode("Employee updated successfully!", StandardCharsets.UTF_8));
            } else {
                request.setAttribute("errorMessage", "Failed to update employee details.");
                request.setAttribute("employee", emp);
                request.getRequestDispatcher("/admin/edit-employee.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid Date of Joining format.");
            request.getRequestDispatcher("/admin/edit-employee.jsp").forward(request, response);
        }
    }

    private void handleDeleteEmployeePost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String employeeId = request.getParameter("employeeId");
        if (employeeId == null || employeeId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/employees?error=missing_id");
            return;
        }

        boolean deleted = employeeService.deleteEmployee(employeeId.trim());
        if (deleted) {
            response.sendRedirect(request.getContextPath() + "/admin/employees?msg=" + 
                                  URLEncoder.encode("Employee " + employeeId + " deleted successfully.", StandardCharsets.UTF_8));
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/employees?error=" + 
                                  URLEncoder.encode("Failed to delete employee " + employeeId + ".", StandardCharsets.UTF_8));
        }
    }
}
