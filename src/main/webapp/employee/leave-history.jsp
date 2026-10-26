<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.LeaveApplication" %>
<%@ page import="java.util.ArrayList" %>
<%
    @SuppressWarnings("unchecked")
    ArrayList<LeaveApplication> leaves = (ArrayList<LeaveApplication>) request.getAttribute("leaves");
    if (leaves == null) leaves = new ArrayList<>();

    String statusFilter = (String) request.getAttribute("statusFilter");
    String leaveTypeFilter = (String) request.getAttribute("leaveTypeFilter");
    String fromDateFilter = (String) request.getAttribute("fromDateFilter");
    String toDateFilter = (String) request.getAttribute("toDateFilter");
    String searchFilter = (String) request.getAttribute("searchFilter");
    String successParam = request.getParameter("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Leave History - ELMS</title>
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
                    <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">My Leave History</h1>
                    <p style="color: #64748b; font-size: 14px;">Track all your submitted leave requests, approval decisions, and statuses</p>
                </div>
                <a href="<%= request.getContextPath() %>/employee/apply-leave" class="btn btn-primary">
                    <span>➕</span> Apply New Leave
                </a>
            </div>

            <% if ("applied".equals(successParam)) { %>
                <div class="alert alert-success">
                    ✅ Your leave application has been submitted successfully and is now PENDING HR approval.
                </div>
            <% } %>

            <!-- Filter and Search Bar -->
            <form action="<%= request.getContextPath() %>/employee/leave-history" method="GET" class="filter-bar">
                
                <div class="filter-item">
                    <label class="form-label" for="search">Search Reason / ID</label>
                    <input type="text" id="search" name="search" class="form-control" 
                           placeholder="Keyword or ID..." value="<%= searchFilter != null ? searchFilter : "" %>">
                </div>

                <div class="filter-item">
                    <label class="form-label" for="status">Status</label>
                    <select id="status" name="status" class="form-control">
                        <option value="ALL" <%= (statusFilter == null || "ALL".equalsIgnoreCase(statusFilter)) ? "selected" : "" %>>All Statuses</option>
                        <option value="PENDING" <%= "PENDING".equalsIgnoreCase(statusFilter) ? "selected" : "" %>>Pending</option>
                        <option value="APPROVED" <%= "APPROVED".equalsIgnoreCase(statusFilter) ? "selected" : "" %>>Approved</option>
                        <option value="REJECTED" <%= "REJECTED".equalsIgnoreCase(statusFilter) ? "selected" : "" %>>Rejected</option>
                    </select>
                </div>

                <div class="filter-item">
                    <label class="form-label" for="leaveType">Leave Type</label>
                    <select id="leaveType" name="leaveType" class="form-control">
                        <option value="ALL" <%= (leaveTypeFilter == null || "ALL".equalsIgnoreCase(leaveTypeFilter)) ? "selected" : "" %>>All Types</option>
                        <option value="CASUAL" <%= "CASUAL".equalsIgnoreCase(leaveTypeFilter) ? "selected" : "" %>>Casual</option>
                        <option value="SICK" <%= "SICK".equalsIgnoreCase(leaveTypeFilter) ? "selected" : "" %>>Sick</option>
                        <option value="EARNED" <%= "EARNED".equalsIgnoreCase(leaveTypeFilter) ? "selected" : "" %>>Earned</option>
                        <option value="EMERGENCY" <%= "EMERGENCY".equalsIgnoreCase(leaveTypeFilter) ? "selected" : "" %>>Emergency</option>
                    </select>
                </div>

                <div class="filter-item">
                    <label class="form-label" for="fromDate">From Date</label>
                    <input type="date" id="fromDate" name="fromDate" class="form-control" 
                           value="<%= fromDateFilter != null ? fromDateFilter : "" %>">
                </div>

                <div class="filter-item">
                    <label class="form-label" for="toDate">To Date</label>
                    <input type="date" id="toDate" name="toDate" class="form-control" 
                           value="<%= toDateFilter != null ? toDateFilter : "" %>">
                </div>

                <div style="display: flex; gap: 8px;">
                    <button type="submit" class="btn btn-primary">
                        🔍 Filter
                    </button>
                    <a href="<%= request.getContextPath() %>/employee/leave-history" class="btn btn-secondary">
                        Reset
                    </a>
                </div>
            </form>

            <!-- Leave History Table Card -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Leave Applications (<%= leaves.size() %> Results)</div>
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
                                    <th>Applied Date</th>
                                    <th>Decision Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (leaves.isEmpty()) { %>
                                    <tr>
                                        <td colspan="9" style="text-align: center; padding: 36px; color: #64748b;">
                                            No leave applications match the selected criteria.
                                        </td>
                                    </tr>
                                <% } else { %>
                                    <% for (LeaveApplication leave : leaves) { %>
                                        <tr>
                                            <td><strong>#<%= leave.getId() %></strong></td>
                                            <td><span class="badge badge-type"><%= leave.getLeaveType() %></span></td>
                                            <td><%= leave.getFromDate() %></td>
                                            <td><%= leave.getToDate() %></td>
                                            <td><strong><%= leave.getDays() %></strong> day(s)</td>
                                            <td style="max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="<%= leave.getReason() %>">
                                                <%= leave.getReason() %>
                                            </td>
                                            <td style="font-size: 13px; color: #64748b;"><%= leave.getAppliedAt() != null ? leave.getAppliedAt().toString().substring(0, 16) : "-" %></td>
                                            <td style="font-size: 13px; color: #64748b;"><%= leave.getApprovedAt() != null ? leave.getApprovedAt().toString().substring(0, 16) : "-" %></td>
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
