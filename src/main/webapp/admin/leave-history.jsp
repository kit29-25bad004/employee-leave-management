<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.LeaveApplication" %>
<%@ page import="java.util.ArrayList" %>
<%
    @SuppressWarnings("unchecked")
    ArrayList<LeaveApplication> leaves = (ArrayList<LeaveApplication>) request.getAttribute("leaves");
    if (leaves == null) leaves = new ArrayList<>();

    String employeeSearch = (String) request.getAttribute("employeeSearch");
    String department = (String) request.getAttribute("department");
    String leaveType = (String) request.getAttribute("leaveType");
    String status = (String) request.getAttribute("status");
    String fromDate = (String) request.getAttribute("fromDate");
    String toDate = (String) request.getAttribute("toDate");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>All Leave Records - ELMS</title>
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
                    <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">Company Leave Records & Audit</h1>
                    <p style="color: #64748b; font-size: 14px;">Historical archive of all employee leave applications and management decisions</p>
                </div>
                <a href="<%= request.getContextPath() %>/admin/leave-requests" class="btn btn-primary">
                    <span>⏳</span> Pending Approvals
                </a>
            </div>

            <!-- Complete Filter Bar -->
            <form action="<%= request.getContextPath() %>/admin/leave-history" method="GET" class="filter-bar">
                
                <div class="filter-item">
                    <label class="form-label" for="employeeSearch">Employee Search</label>
                    <input type="text" id="employeeSearch" name="employeeSearch" class="form-control" 
                           placeholder="Emp ID or Name..." value="<%= employeeSearch != null ? employeeSearch : "" %>">
                </div>

                <div class="filter-item">
                    <label class="form-label" for="department">Department</label>
                    <select id="department" name="department" class="form-control">
                        <option value="ALL" <%= (department == null || "ALL".equalsIgnoreCase(department)) ? "selected" : "" %>>All Departments</option>
                        <option value="Engineering" <%= "Engineering".equalsIgnoreCase(department) ? "selected" : "" %>>Engineering</option>
                        <option value="Human Resources" <%= "Human Resources".equalsIgnoreCase(department) ? "selected" : "" %>>Human Resources</option>
                        <option value="Finance" <%= "Finance".equalsIgnoreCase(department) ? "selected" : "" %>>Finance</option>
                        <option value="Marketing" <%= "Marketing".equalsIgnoreCase(department) ? "selected" : "" %>>Marketing</option>
                        <option value="Operations" <%= "Operations".equalsIgnoreCase(department) ? "selected" : "" %>>Operations</option>
                        <option value="Product" <%= "Product".equalsIgnoreCase(department) ? "selected" : "" %>>Product</option>
                    </select>
                </div>

                <div class="filter-item">
                    <label class="form-label" for="leaveType">Leave Type</label>
                    <select id="leaveType" name="leaveType" class="form-control">
                        <option value="ALL" <%= (leaveType == null || "ALL".equalsIgnoreCase(leaveType)) ? "selected" : "" %>>All Categories</option>
                        <option value="CASUAL" <%= "CASUAL".equalsIgnoreCase(leaveType) ? "selected" : "" %>>Casual</option>
                        <option value="SICK" <%= "SICK".equalsIgnoreCase(leaveType) ? "selected" : "" %>>Sick</option>
                        <option value="EARNED" <%= "EARNED".equalsIgnoreCase(leaveType) ? "selected" : "" %>>Earned</option>
                        <option value="EMERGENCY" <%= "EMERGENCY".equalsIgnoreCase(leaveType) ? "selected" : "" %>>Emergency</option>
                    </select>
                </div>

                <div class="filter-item">
                    <label class="form-label" for="status">Decision Status</label>
                    <select id="status" name="status" class="form-control">
                        <option value="ALL" <%= (status == null || "ALL".equalsIgnoreCase(status)) ? "selected" : "" %>>All Statuses</option>
                        <option value="PENDING" <%= "PENDING".equalsIgnoreCase(status) ? "selected" : "" %>>Pending</option>
                        <option value="APPROVED" <%= "APPROVED".equalsIgnoreCase(status) ? "selected" : "" %>>Approved</option>
                        <option value="REJECTED" <%= "REJECTED".equalsIgnoreCase(status) ? "selected" : "" %>>Rejected</option>
                    </select>
                </div>

                <div class="filter-item">
                    <label class="form-label" for="fromDate">From Date</label>
                    <input type="date" id="fromDate" name="fromDate" class="form-control" 
                           value="<%= fromDate != null ? fromDate : "" %>">
                </div>

                <div class="filter-item">
                    <label class="form-label" for="toDate">To Date</label>
                    <input type="date" id="toDate" name="toDate" class="form-control" 
                           value="<%= toDate != null ? toDate : "" %>">
                </div>

                <div style="display: flex; gap: 8px;">
                    <button type="submit" class="btn btn-primary">
                        🔍 Filter
                    </button>
                    <a href="<%= request.getContextPath() %>/admin/leave-history" class="btn btn-secondary">
                        Reset
                    </a>
                </div>
            </form>

            <!-- Table of Historical Leave Records -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Leave History Audit Log (<%= leaves.size() %> Records)</div>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Leave ID</th>
                                    <th>Employee</th>
                                    <th>Department</th>
                                    <th>Leave Type</th>
                                    <th>From Date</th>
                                    <th>To Date</th>
                                    <th>Days</th>
                                    <th>Reason</th>
                                    <th>Applied On</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (leaves.isEmpty()) { %>
                                    <tr>
                                        <td colspan="10" style="text-align: center; padding: 40px; color: #64748b;">
                                            No historical leave applications found matching the selected filters.
                                        </td>
                                    </tr>
                                <% } else { %>
                                    <% for (LeaveApplication leave : leaves) { %>
                                        <tr>
                                            <td><strong>#<%= leave.getId() %></strong></td>
                                            <td>
                                                <div style="font-weight: 600;"><%= leave.getEmployeeName() != null ? leave.getEmployeeName() : "Employee" %></div>
                                                <small style="color: #64748b;"><%= leave.getEmployeeId() %></small>
                                            </td>
                                            <td><%= leave.getDepartment() != null ? leave.getDepartment() : "General" %></td>
                                            <td><span class="badge badge-type"><%= leave.getLeaveType() %></span></td>
                                            <td><%= leave.getFromDate() %></td>
                                            <td><%= leave.getToDate() %></td>
                                            <td><strong><%= leave.getDays() %></strong> day(s)</td>
                                            <td style="max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="<%= leave.getReason() %>">
                                                <%= leave.getReason() %>
                                            </td>
                                            <td style="font-size: 13px; color: #64748b;">
                                                <%= leave.getAppliedAt() != null ? leave.getAppliedAt().toString().substring(0, 16) : "-" %>
                                            </td>
                                            <td>
                                                <% if (leave.isPending()) { %>
                                                    <span class="badge badge-pending">PENDING</span>
                                                <% } else if (leave.isApproved()) { %>
                                                    <span class="badge badge-approved">APPROVED</span>
                                                <% } else { %>
                                                    <span class="badge badge-rejected">REJECTED</span>
                                                <% } %>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

<jsp:include page="/common/footer.jsp" />
