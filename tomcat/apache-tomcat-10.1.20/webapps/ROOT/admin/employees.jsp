<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.Employee" %>
<%@ page import="java.util.ArrayList" %>
<%
    @SuppressWarnings("unchecked")
    ArrayList<Employee> employees = (ArrayList<Employee>) request.getAttribute("employees");
    if (employees == null) employees = new ArrayList<>();

    String searchEmpId = (String) request.getAttribute("searchEmpId");
    String searchName = (String) request.getAttribute("searchName");
    String searchDept = (String) request.getAttribute("searchDept");
    String msg = request.getParameter("msg");
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Management - ELMS</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>

<div class="app-layout">
    <jsp:include page="/common/sidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/common/header.jsp" />

        <div class="content-wrapper">

            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
                <div>
                    <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">Employee Directory</h1>
                    <p style="color: #64748b; font-size: 14px;">Manage company employees, personnel records, and accounts</p>
                </div>
                <a href="<%= request.getContextPath() %>/admin/add-employee" class="btn btn-primary">
                    <span>➕</span> Add New Employee
                </a>
            </div>

            <% if (msg != null) { %>
                <div class="alert alert-success">✅ <%= msg %></div>
            <% } %>

            <% if (error != null) { %>
                <div class="alert alert-danger">❌ <%= error %></div>
            <% } %>

            <!-- Employee Search Bar -->
            <form action="<%= request.getContextPath() %>/admin/employees" method="GET" class="filter-bar">
                
                <div class="filter-item">
                    <label class="form-label" for="employeeId">Employee ID</label>
                    <input type="text" id="employeeId" name="employeeId" class="form-control" 
                           placeholder="Search EMP..." value="<%= searchEmpId != null ? searchEmpId : "" %>">
                </div>

                <div class="filter-item">
                    <label class="form-label" for="name">Employee Name</label>
                    <input type="text" id="name" name="name" class="form-control" 
                           placeholder="Search full name..." value="<%= searchName != null ? searchName : "" %>">
                </div>

                <div class="filter-item">
                    <label class="form-label" for="department">Department</label>
                    <input type="text" id="department" name="department" class="form-control" 
                           placeholder="e.g. Engineering..." value="<%= searchDept != null ? searchDept : "" %>">
                </div>

                <div style="display: flex; gap: 8px;">
                    <button type="submit" class="btn btn-primary">
                        🔍 Search
                    </button>
                    <a href="<%= request.getContextPath() %>/admin/employees" class="btn btn-secondary">
                        Reset
                    </a>
                </div>
            </form>

            <!-- Employees Table Card -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Registered Personnel (<%= employees.size() %> Employees)</div>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Emp ID</th>
                                    <th>Full Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Department</th>
                                    <th>Designation</th>
                                    <th>Joining Date</th>
                                    <th style="text-align: right;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (employees.isEmpty()) { %>
                                    <tr>
                                        <td colspan="8" style="text-align: center; padding: 36px; color: #64748b;">
                                            No employee records found matching your search.
                                        </td>
                                    </tr>
                                <% } else { %>
                                    <% for (Employee emp : employees) { %>
                                        <tr>
                                            <td><strong><%= emp.getEmployeeId() %></strong></td>
                                            <td><div style="font-weight: 600;"><%= emp.getFullName() %></div></td>
                                            <td><%= emp.getEmail() %></td>
                                            <td><%= emp.getPhone() %></td>
                                            <td><span class="badge" style="background: #f1f5f9; color: #334155;"><%= emp.getDepartment() %></span></td>
                                            <td><%= emp.getDesignation() %></td>
                                            <td><%= emp.getDateOfJoining() %></td>
                                            <td style="text-align: right; white-space: nowrap;">
                                                <a href="<%= request.getContextPath() %>/admin/edit-employee?employeeId=<%= emp.getEmployeeId() %>" 
                                                   class="btn btn-secondary btn-sm" title="Edit Employee">
                                                    ✏️ Edit
                                                </a>

                                                <button type="button" class="btn btn-danger btn-sm" 
                                                        onclick="confirmEmployeeDelete('<%= emp.getEmployeeId() %>', '<%= emp.getFullName().replace("'", "\\'") %>')" 
                                                        title="Delete Employee">
                                                    🗑️ Delete
                                                </button>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Confirmation Modal for Safe Deletion -->
            <div id="deleteConfirmModal" class="modal-overlay">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3>Confirm Employee Deletion</h3>
                        <button type="button" onclick="closeDeleteModal()" style="background:none; border:none; font-size: 20px; cursor: pointer;">&times;</button>
                    </div>
                    <div class="modal-body">
                        <p>Are you sure you want to permanently delete employee <strong id="deleteEmployeeName"></strong>?</p>
                        <p style="margin-top: 10px; color: #dc2626; font-size: 13px;">
                            ⚠️ Warning: This will cascade delete their login user credentials, leave quota balances, leave history, and notifications safely within a database transaction.
                        </p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" onclick="closeDeleteModal()">Cancel</button>
                        <form id="deleteForm" action="<%= request.getContextPath() %>/admin/delete-employee" method="POST" style="margin: 0;">
                            <input type="hidden" id="deleteEmployeeIdInput" name="employeeId" value="">
                            <button type="submit" class="btn btn-danger">Confirm Permanent Deletion</button>
                        </form>
                    </div>
                </div>
            </div>

<jsp:include page="/common/footer.jsp" />
