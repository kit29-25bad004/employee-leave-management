<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.LeaveApplication" %>
<%@ page import="java.util.ArrayList" %>
<%
    @SuppressWarnings("unchecked")
    ArrayList<LeaveApplication> pendingLeaves = (ArrayList<LeaveApplication>) request.getAttribute("pendingLeaves");
    if (pendingLeaves == null) pendingLeaves = new ArrayList<>();

    String msg = request.getParameter("msg");
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pending Leave Requests - ELMS</title>
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
                    <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">Pending Leave Requests Queue</h1>
                    <p style="color: #64748b; font-size: 14px;">Review pending applications, inspect details, and issue approvals or rejections</p>
                </div>
                <a href="<%= request.getContextPath() %>/admin/leave-history" class="btn btn-secondary">
                    View Complete Leave History &rarr;
                </a>
            </div>

            <% if (msg != null) { %>
                <div class="alert alert-success">✅ <%= msg %></div>
            <% } %>

            <% if (error != null) { %>
                <div class="alert alert-danger">❌ <%= error %></div>
            <% } %>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">Pending Approvals Queue (<%= pendingLeaves.size() %> Applications)</div>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Leave ID</th>
                                    <th>Employee ID</th>
                                    <th>Employee Name</th>
                                    <th>Department</th>
                                    <th>Leave Type</th>
                                    <th>From Date</th>
                                    <th>To Date</th>
                                    <th>Days</th>
                                    <th>Reason</th>
                                    <th>Applied Date</th>
                                    <th>Status</th>
                                    <th style="text-align: center;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (pendingLeaves.isEmpty()) { %>
                                    <tr>
                                        <td colspan="12" style="text-align: center; padding: 48px; color: #64748b;">
                                            <div style="font-size: 32px; margin-bottom: 10px;">🎉</div>
                                            <strong>All caught up!</strong> There are currently no pending leave applications awaiting review.
                                        </td>
                                    </tr>
                                <% } else { %>
                                    <% for (LeaveApplication leave : pendingLeaves) { %>
                                        <tr>
                                            <td><strong>#<%= leave.getId() %></strong></td>
                                            <td><strong><%= leave.getEmployeeId() %></strong></td>
                                            <td><%= leave.getEmployeeName() != null ? leave.getEmployeeName() : "Employee" %></td>
                                            <td><span class="badge" style="background: #f1f5f9; color: #334155;"><%= leave.getDepartment() != null ? leave.getDepartment() : "General" %></span></td>
                                            <td><span class="badge badge-type"><%= leave.getLeaveType() %></span></td>
                                            <td><%= leave.getFromDate() %></td>
                                            <td><%= leave.getToDate() %></td>
                                            <td><strong><%= leave.getDays() %></strong> day(s)</td>
                                            <td style="max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="<%= leave.getReason() %>">
                                                <%= leave.getReason() %>
                                            </td>
                                            <td style="font-size: 13px; color: #64748b;"><%= leave.getAppliedAt() != null ? leave.getAppliedAt().toString().substring(0, 16) : "-" %></td>
                                            <td><span class="badge badge-pending">PENDING</span></td>
                                            <td style="text-align: center; white-space: nowrap;">
                                                <div style="display: inline-flex; gap: 8px;">
                                                    <!-- Approve Button Form -->
                                                    <form action="<%= request.getContextPath() %>/admin/leave-action" method="POST" style="margin: 0;">
                                                        <input type="hidden" name="leaveId" value="<%= leave.getId() %>">
                                                        <input type="hidden" name="action" value="APPROVE">
                                                        <button type="submit" class="btn btn-success btn-sm" title="Approve Leave and Deduct Balance">
                                                            ✓ Approve
                                                        </button>
                                                    </form>

                                                    <!-- Reject Button Form -->
                                                    <form action="<%= request.getContextPath() %>/admin/leave-action" method="POST" style="margin: 0;">
                                                        <input type="hidden" name="leaveId" value="<%= leave.getId() %>">
                                                        <input type="hidden" name="action" value="REJECT">
                                                        <button type="submit" class="btn btn-danger btn-sm" title="Reject Leave Application">
                                                            ✕ Reject
                                                        </button>
                                                    </form>
                                                </div>
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
