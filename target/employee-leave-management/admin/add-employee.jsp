<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String errorMsg = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Employee - ELMS</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>

<div class="app-layout">
    <jsp:include page="/common/sidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/common/header.jsp" />

        <div class="content-wrapper">

            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: gap; gap: 12px;">
                <div>
                    <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">Add New Employee</h1>
                    <p style="color: #64748b; font-size: 14px;">Register an employee, configure login account, and initialize leave quotas</p>
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
                    <div class="card-title">Employee Registration Form</div>
                </div>
                <div class="card-body">
                    <form action="<%= request.getContextPath() %>/admin/add-employee" method="POST" class="needs-loading-state">
                        <div class="form-grid">
                            
                            <div class="form-group">
                                <label class="form-label" for="employeeId">Employee ID <span class="required">*</span></label>
                                <input type="text" id="employeeId" name="employeeId" class="form-control" 
                                       placeholder="e.g. EMP003" required style="text-transform: uppercase;">
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="fullName">Full Name <span class="required">*</span></label>
                                <input type="text" id="fullName" name="fullName" class="form-control" 
                                       placeholder="e.g. Robert Smith" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="email">Work Email Address <span class="required">*</span></label>
                                <input type="email" id="email" name="email" class="form-control" 
                                       placeholder="e.g. robert.smith@company.com" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="phone">Phone Number <span class="required">*</span></label>
                                <input type="tel" id="phone" name="phone" class="form-control" 
                                       placeholder="e.g. 9876543213" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="department">Department <span class="required">*</span></label>
                                <select id="department" name="department" class="form-control" required>
                                    <option value="">-- Select Department --</option>
                                    <option value="Engineering">Engineering</option>
                                    <option value="Human Resources">Human Resources</option>
                                    <option value="Finance">Finance</option>
                                    <option value="Marketing">Marketing</option>
                                    <option value="Operations">Operations</option>
                                    <option value="Product">Product</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="designation">Designation <span class="required">*</span></label>
                                <input type="text" id="designation" name="designation" class="form-control" 
                                       placeholder="e.g. Software Engineer" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="dateOfJoining">Date of Joining <span class="required">*</span></label>
                                <input type="date" id="dateOfJoining" name="dateOfJoining" class="form-control" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="password">Account Password <span class="required">*</span></label>
                                <input type="password" id="password" name="password" class="form-control" 
                                       placeholder="Enter initial password (stored via SHA-256)" required>
                            </div>

                        </div>

                        <div style="background: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 6px; padding: 14px; margin-top: 10px; margin-bottom: 20px; font-size: 13px; color: #475569;">
                            ℹ️ <strong>System Automation Notice:</strong> Upon registration, the database transaction will atomically:
                            <ul style="margin-left: 20px; margin-top: 6px;">
                                <li>Create the employee profile record</li>
                                <li>Create the user login account with SHA-256 hashed password</li>
                                <li>Initialize standard leave allowances: <strong>12 Casual, 10 Sick, 15 Earned, 5 Emergency</strong></li>
                                <li>Dispatch an onboarding notification to the employee</li>
                            </ul>
                        </div>

                        <div style="display: flex; justify-content: flex-end; gap: 12px;">
                            <a href="<%= request.getContextPath() %>/admin/employees" class="btn btn-secondary">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <span>➕</span> Create Employee & Account
                            </button>
                        </div>
                    </form>
                </div>
            </div>

<jsp:include page="/common/footer.jsp" />
