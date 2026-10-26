<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.Employee" %>
<%
    Employee emp = (Employee) request.getAttribute("employee");
    String successMsg = (String) request.getAttribute("successMessage");
    String errorMsg = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - ELMS</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>

<div class="app-layout">
    <jsp:include page="/common/sidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/common/header.jsp" />

        <div class="content-wrapper">

            <div style="margin-bottom: 24px;">
                <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">Employee Profile</h1>
                <p style="color: #64748b; font-size: 14px;">View your personnel details and update contact information</p>
            </div>

            <% if (successMsg != null) { %>
                <div class="alert alert-success">✅ <%= successMsg %></div>
            <% } %>

            <% if (errorMsg != null) { %>
                <div class="alert alert-danger">❌ <%= errorMsg %></div>
            <% } %>

            <div class="card" style="max-width: 800px;">
                <div class="card-header">
                    <div class="card-title">Personal & Contact Details</div>
                </div>
                <div class="card-body">
                    <form action="<%= request.getContextPath() %>/employee/profile" method="POST" class="needs-loading-state">
                        <div class="form-grid">
                            
                            <!-- Readonly Employee ID -->
                            <div class="form-group">
                                <label class="form-label">Employee ID (System Fixed)</label>
                                <input type="text" class="form-control" value="<%= emp != null ? emp.getEmployeeId() : "" %>" readonly disabled>
                            </div>

                            <!-- Readonly Department -->
                            <div class="form-group">
                                <label class="form-label">Department</label>
                                <input type="text" class="form-control" value="<%= emp != null ? emp.getDepartment() : "" %>" readonly disabled>
                            </div>

                            <!-- Readonly Designation -->
                            <div class="form-group">
                                <label class="form-label">Designation</label>
                                <input type="text" class="form-control" value="<%= emp != null ? emp.getDesignation() : "" %>" readonly disabled>
                            </div>

                            <!-- Readonly Joining Date -->
                            <div class="form-group">
                                <label class="form-label">Date of Joining</label>
                                <input type="text" class="form-control" value="<%= emp != null ? emp.getDateOfJoining() : "" %>" readonly disabled>
                            </div>

                            <!-- Editable Full Name -->
                            <div class="form-group">
                                <label class="form-label" for="fullName">Full Name <span class="required">*</span></label>
                                <input type="text" id="fullName" name="fullName" class="form-control" 
                                       value="<%= emp != null ? emp.getFullName() : "" %>" required>
                            </div>

                            <!-- Editable Email -->
                            <div class="form-group">
                                <label class="form-label" for="email">Work Email <span class="required">*</span></label>
                                <input type="email" id="email" name="email" class="form-control" 
                                       value="<%= emp != null ? emp.getEmail() : "" %>" required>
                            </div>

                            <!-- Editable Phone -->
                            <div class="form-group full-width">
                                <label class="form-label" for="phone">Phone Number <span class="required">*</span></label>
                                <input type="tel" id="phone" name="phone" class="form-control" 
                                       value="<%= emp != null ? emp.getPhone() : "" %>" required>
                            </div>

                        </div>

                        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 10px;">
                            <a href="<%= request.getContextPath() %>/employee/dashboard" class="btn btn-secondary">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <span>💾</span> Save Profile Changes
                            </button>
                        </div>
                    </form>
                </div>
            </div>

<jsp:include page="/common/footer.jsp" />
