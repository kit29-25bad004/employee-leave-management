<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.Employee" %>
<%
    Employee emp = (Employee) request.getAttribute("employee");
    String errorMsg = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Employee - ELMS</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>

<div class="app-layout">
    <jsp:include page="/common/sidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/common/header.jsp" />

        <div class="content-wrapper">

            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
                <div>
                    <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">Edit Employee</h1>
                    <p style="color: #64748b; font-size: 14px;">Modify personnel details for <%= emp != null ? emp.getFullName() : "" %></p>
                </div>
                <a href="<%= request.getContextPath() %>/admin/employees" class="btn btn-secondary">
                    &larr; Back to Employees
                </a>
            </div>

            <% if (errorMsg != null) { %>
                <div class="alert alert-danger">❌ <%= errorMsg %></div>
            <% } %>

            <div class="card" style="max-width: 860px;">
                <div class="card-header">
                    <div class="card-title">Employee Details</div>
                </div>
                <div class="card-body">
                    <form action="<%= request.getContextPath() %>/admin/edit-employee" method="POST" class="needs-loading-state">
                        <div class="form-grid">
                            
                            <div class="form-group">
                                <label class="form-label" for="employeeId">Employee ID (System Fixed)</label>
                                <input type="text" id="employeeId" name="employeeId" class="form-control" 
                                       value="<%= emp != null ? emp.getEmployeeId() : "" %>" readonly>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="fullName">Full Name <span class="required">*</span></label>
                                <input type="text" id="fullName" name="fullName" class="form-control" 
                                       value="<%= emp != null ? emp.getFullName() : "" %>" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="email">Work Email <span class="required">*</span></label>
                                <input type="email" id="email" name="email" class="form-control" 
                                       value="<%= emp != null ? emp.getEmail() : "" %>" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="phone">Phone Number <span class="required">*</span></label>
                                <input type="tel" id="phone" name="phone" class="form-control" 
                                       value="<%= emp != null ? emp.getPhone() : "" %>" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="department">Department <span class="required">*</span></label>
                                <select id="department" name="department" class="form-control" required>
                                    <% 
                                        String curDept = emp != null ? emp.getDepartment() : "";
                                        String[] depts = {"Engineering", "Human Resources", "Finance", "Marketing", "Operations", "Product"};
                                        for (String d : depts) {
                                    %>
                                        <option value="<%= d %>" <%= d.equalsIgnoreCase(curDept) ? "selected" : "" %>><%= d %></option>
                                    <% } %>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="designation">Designation <span class="required">*</span></label>
                                <input type="text" id="designation" name="designation" class="form-control" 
                                       value="<%= emp != null ? emp.getDesignation() : "" %>" required>
                            </div>

                            <div class="form-group full-width">
                                <label class="form-label" for="dateOfJoining">Date of Joining <span class="required">*</span></label>
                                <input type="date" id="dateOfJoining" name="dateOfJoining" class="form-control" 
                                       value="<%= emp != null && emp.getDateOfJoining() != null ? emp.getDateOfJoining().toString() : "" %>" required>
                            </div>

                        </div>

                        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 10px;">
                            <a href="<%= request.getContextPath() %>/admin/employees" class="btn btn-secondary">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <span>💾</span> Save Changes
                            </button>
                        </div>
                    </form>
                </div>
            </div>

<jsp:include page="/common/footer.jsp" />
