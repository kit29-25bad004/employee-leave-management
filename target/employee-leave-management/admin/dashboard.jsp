<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.LeaveApplication" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%
    @SuppressWarnings("unchecked")
    HashMap<String, Integer> stats = (HashMap<String, Integer>) request.getAttribute("stats");
    @SuppressWarnings("unchecked")
    ArrayList<LeaveApplication> recentLeaves = (ArrayList<LeaveApplication>) request.getAttribute("recentLeaves");

    if (stats == null) stats = new HashMap<>();
    if (recentLeaves == null) recentLeaves = new ArrayList<>();

    int totalEmployees = stats.getOrDefault("TOTAL_EMPLOYEES", 0);
    int totalRequests = stats.getOrDefault("TOTAL_REQUESTS", 0);
    int pendingRequests = stats.getOrDefault("PENDING_REQUESTS", 0);
    int approvedRequests = stats.getOrDefault("APPROVED_REQUESTS", 0);
    int rejectedRequests = stats.getOrDefault("REJECTED_REQUESTS", 0);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - ELMS</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>

<div class="app-layout">
    <jsp:include page="/common/sidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/common/header.jsp" />

        <div class="content-wrapper">

            <!-- Admin Welcome Banner -->
            <div class="welcome-banner" style="background: linear-gradient(135deg, #0f172a, #1e3a8a);">
                <div class="welcome-text">
                    <h1>Executive HR Management Portal 🛡️</h1>
                    <p>
                        Welcome, <strong><%= session.getAttribute("fullName") %></strong> &bull; 
                        Role: <strong>Administrator</strong> &bull; Total Staff Members: <strong><%= totalEmployees %></strong>
                    </p>
                </div>
                <div class="welcome-actions">
                    <a href="<%= request.getContextPath() %>/admin/leave-requests" class="btn btn-primary" style="background: #f59e0b; border-color: #d97706; color: #ffffff; font-weight: 700;">
                        <span>⏳</span> Pending Approvals (<%= pendingRequests %>)
                    </a>
                    <a href="<%= request.getContextPath() %>/admin/add-employee" class="btn btn-secondary" style="background: rgba(255,255,255,0.15); color: #ffffff; border-color: rgba(255,255,255,0.3);">
                        <span>➕</span> Add New Employee
                    </a>
                </div>
            </div>

            <!-- Dynamic Statistics Cards from MySQL -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= totalEmployees %></h3>
                        <p>Total Employees</p>
                    </div>
                    <div class="stat-icon stat-purple">👥</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= totalRequests %></h3>
                        <p>Total Applications</p>
                    </div>
                    <div class="stat-icon stat-blue">📋</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= pendingRequests %></h3>
                        <p>Pending Review</p>
                    </div>
                    <div class="stat-icon stat-amber">⏳</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= approvedRequests %></h3>
                        <p>Approved Leaves</p>
                    </div>
                    <div class="stat-icon stat-green">✅</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= rejectedRequests %></h3>
                        <p>Rejected Leaves</p>
                    </div>
                    <div class="stat-icon stat-red">❌</div>
                </div>
            </div>

            <!-- Recent Requests with Quick Action Buttons -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Recent Leave Requests (Overview)</div>
                    <div style="display: flex; gap: 8px;">
                        <a href="<%= request.getContextPath() %>/admin/leave-requests" class="btn btn-primary btn-sm">
                            Review All Pending &rarr;
                        </a>
                        <a href="<%= request.getContextPath() %>/admin/leave-history" class="btn btn-secondary btn-sm">
                            Full Audit History &rarr;
                        </a>
                    </div>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Leave ID</th>
                                    <th>Employee</th>
                                    <th>Department</th>
                                    <th>Type</th>
                                    <th>Dates</th>
                                    <th>Days</th>
                                    <th>Reason</th>
                                    <th>Status</th>
                                    <th>Quick Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (recentLeaves.isEmpty()) { %>
                                    <tr>
                                        <td colspan="9" style="text-align: center; padding: 36px; color: #64748b;">
                                            No leave requests recorded yet.
                                        </td>
                                    </tr>
                                <% } else { %>
                                    <% for (LeaveApplication leave : recentLeaves) { %>
                                        <tr>
                                            <td><strong>#<%= leave.getId() %></strong></td>
                                            <td>
                                                <div style="font-weight: 600;"><%= leave.getEmployeeName() != null ? leave.getEmployeeName() : "Employee" %></div>
                                                <small style="color: #64748b;"><%= leave.getEmployeeId() %></small>
                                            </td>
                                            <td><%= leave.getDepartment() != null ? leave.getDepartment() : "General" %></td>
                                            <td><span class="badge badge-type"><%= leave.getLeaveType() %></span></td>
                                            <td style="white-space: nowrap;"><%= leave.getFromDate() %> &rarr; <%= leave.getToDate() %></td>
                                            <td><strong><%= leave.getDays() %></strong> d</td>
                                            <td style="max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="<%= leave.getReason() %>">
                                                <%= leave.getReason() %>
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
                                            <td>
                                                <% if (leave.isPending()) { %>
                                                    <div style="display: flex; gap: 6px;">
                                                        <form action="<%= request.getContextPath() %>/admin/leave-action" method="POST" style="display: inline;">
                                                            <input type="hidden" name="leaveId" value="<%= leave.getId() %>">
                                                            <input type="hidden" name="action" value="APPROVE">
                                                            <button type="submit" class="btn btn-success btn-sm" title="Approve Leave">
                                                                ✓ Approve
                                                            </button>
                                                        </form>
                                                        <form action="<%= request.getContextPath() %>/admin/leave-action" method="POST" style="display: inline;">
                                                            <input type="hidden" name="leaveId" value="<%= leave.getId() %>">
                                                            <input type="hidden" name="action" value="REJECT">
                                                            <button type="submit" class="btn btn-danger btn-sm" title="Reject Leave">
                                                                ✕ Reject
                                                            </button>
                                                        </form>
                                                    </div>
                                                <% } else { %>
                                                    <span style="font-size: 12px; color: #64748b;">Processed</span>
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
