package com.elm.servlet;

import com.elm.dao.EmployeeDAO;
import com.elm.model.Employee;
import com.elm.service.EmployeeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles employee profile viewing and updates.
 */
@WebServlet("/employee/profile")
public class ProfileServlet extends HttpServlet {

    private final EmployeeService employeeService = new EmployeeService();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String employeeId = (String) session.getAttribute("employeeId");
        Employee employee = employeeDAO.getByEmployeeId(employeeId);

        request.setAttribute("employee", employee);
        request.getRequestDispatcher("/employee/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Always identify employee via session, never from request parameters
        String employeeId = (String) session.getAttribute("employeeId");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        if (fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty()) {
            request.setAttribute("errorMessage", "All profile fields are required.");
            Employee current = employeeDAO.getByEmployeeId(employeeId);
            request.setAttribute("employee", current);
            request.getRequestDispatcher("/employee/profile.jsp").forward(request, response);
            return;
        }

        boolean updated = employeeService.updateProfile(employeeId, fullName.trim(), email.trim(), phone.trim());

        if (updated) {
            session.setAttribute("fullName", fullName.trim());
            request.setAttribute("successMessage", "Profile updated successfully!");
        } else {
            request.setAttribute("errorMessage", "Failed to update profile. Email might already be taken by another user.");
        }

        Employee refreshedEmployee = employeeDAO.getByEmployeeId(employeeId);
        request.setAttribute("employee", refreshedEmployee);
        request.getRequestDispatcher("/employee/profile.jsp").forward(request, response);
    }
}
