<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.Employee" %>
<%@ page import="com.elm.model.LeaveApplication" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%
    Employee emp = (Employee) request.getAttribute("employee");
    @SuppressWarnings("unchecked")
    HashMap<String, Integer> stats = (HashMap<String, Integer>) request.getAttribute("stats");
    @SuppressWarnings("unchecked")
    ArrayList<LeaveApplication> recentLeaves = (ArrayList<LeaveApplication>) request.getAttribute("recentLeaves");

    if (stats == null) stats = new HashMap<>();
    if (recentLeaves == null) recentLeaves = new ArrayList<>();
    
    int totalLeaves = stats.getOrDefault("TOTAL_LEAVES", 0);
    int approvedLeaves = stats.getOrDefault("APPROVED_LEAVES", 0);
    int pendingLeaves = stats.getOrDefault("PENDING_LEAVES", 0);
    int rejectedLeaves = stats.getOrDefault("REJECTED_LEAVES", 0);
    int remainingLeaves = stats.getOrDefault("REMAINING_LEAVES", 0);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Dashboard - ELMS</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>

<div class="app-layout">
    <jsp:include page="/common/sidebar.jsp" />

    <div class="main-content">
        <jsp:include page="/common/header.jsp" />

        <div class="content-wrapper">

            <!-- Welcome Banner -->
            <div class="welcome-banner">
                <div class="welcome-text">
                    <h1>Welcome back, <%= emp != null ? emp.getFullName() : session.getAttribute("fullName") %>! 👋</h1>
                    <p>
                        Employee ID: <strong><%= emp != null ? emp.getEmployeeId() : session.getAttribute("employeeId") %></strong> &bull; 
                        Department: <strong><%= emp != null ? emp.getDepartment() : "General" %></strong> &bull; 
                        Designation: <strong><%= emp != null ? emp.getDesignation() : "Staff" %></strong>
                    </p>
                </div>
                <div class="welcome-actions">
                    <a href="<%= request.getContextPath() %>/employee/apply-leave" class="btn btn-primary" style="background: #ffffff; color: #1e3a8a; font-weight: 700;">
                        <span>➕</span> Apply for Leave
                    </a>
                    <a href="<%= request.getContextPath() %>/employee/leave-balance" class="btn btn-secondary" style="background: rgba(255,255,255,0.2); color: #ffffff; border-color: rgba(255,255,255,0.3);">
                        <span>💳</span> Check Balance
                    </a>
                </div>
            </div>

            <!-- 5 Dynamic Statistics Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= totalLeaves %></h3>
                        <p>Total Applied</p>
                    </div>
                    <div class="stat-icon stat-blue">📊</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= approvedLeaves %></h3>
                        <p>Approved Leaves</p>
                    </div>
                    <div class="stat-icon stat-green">✅</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= pendingLeaves %></h3>
                        <p>Pending Requests</p>
                    </div>
                    <div class="stat-icon stat-amber">⏳</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= rejectedLeaves %></h3>
                        <p>Rejected Requests</p>
                    </div>
                    <div class="stat-icon stat-red">❌</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <h3><%= remainingLeaves %></h3>
                        <p>Remaining Quota (Days)</p>
                    </div>
                    <div class="stat-icon stat-teal">🎯</div>
                </div>
            </div>

            <!-- Recent Applications Table Card -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Recent Leave Applications</div>
                    <a href="<%= request.getContextPath() %>/employee/leave-history" class="btn btn-secondary btn-sm">
                        View Full History &rarr;
                    </a>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Leave ID</th>
                                    <th>Leave Type</th>
                                    <th>From Date</th>
                                    <th>To Date</th>
                                    <th>Days</th>
                                    <th>Reason</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (recentLeaves.isEmpty()) { %>
                                    <tr>
                                        <td colspan="7" style="text-align: center; padding: 32px; color: #64748b;">
                                            No recent leave applications found. Click <strong>Apply for Leave</strong> to submit one.
                                        </td>
                                    </tr>
                                <% } else { %>
                                    <% for (LeaveApplication leave : recentLeaves) { %>
                                        <tr>
                                            <td><strong>#<%= leave.getId() %></strong></td>
                                            <td><span class="badge badge-type"><%= leave.getLeaveType() %></span></td>
                                            <td><%= leave.getFromDate() %></td>
                                            <td><%= leave.getToDate() %></td>
                                            <td><strong><%= leave.getDays() %></strong> day(s)</td>
                                            <td style="max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="<%= leave.getReason() %>">
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
                                        </tr>
                                    <% } %>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

<jsp:include page="/common/footer.jsp" />
