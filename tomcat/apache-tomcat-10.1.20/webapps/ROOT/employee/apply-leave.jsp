<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.LeaveBalance" %>
<%@ page import="java.util.HashMap" %>
<%
    LeaveBalance balance = (LeaveBalance) request.getAttribute("balance");
    @SuppressWarnings("unchecked")
    HashMap<String, Integer> balanceMap = (HashMap<String, Integer>) request.getAttribute("balanceMap");

    int casual = balance != null ? balance.getCasualLeave() : 0;
    int sick = balance != null ? balance.getSickLeave() : 0;
    int earned = balance != null ? balance.getEarnedLeave() : 0;
    int emergency = balance != null ? balance.getEmergencyLeave() : 0;

    String errorMsg = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apply for Leave - ELMS</title>
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
                    <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">Apply for Leave</h1>
                    <p style="color: #64748b; font-size: 14px;">Submit a new leave application for HR manager approval</p>
                </div>
                <a href="<%= request.getContextPath() %>/employee/leave-history" class="btn btn-secondary">
                    View Leave History
                </a>
            </div>

            <!-- Current Quotas Quick Glance -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin-bottom: 24px;">
                <div style="background: #ffffff; border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px 18px;">
                    <div style="font-size: 12px; color: #64748b; font-weight: 600; text-transform: uppercase;">Casual Available</div>
                    <div style="font-size: 22px; font-weight: 700; color: #2563eb;"><%= casual %> <span style="font-size: 13px; font-weight: normal; color: #64748b;">days</span></div>
                </div>
                <div style="background: #ffffff; border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px 18px;">
                    <div style="font-size: 12px; color: #64748b; font-weight: 600; text-transform: uppercase;">Sick Available</div>
                    <div style="font-size: 22px; font-weight: 700; color: #0f766e;"><%= sick %> <span style="font-size: 13px; font-weight: normal; color: #64748b;">days</span></div>
                </div>
                <div style="background: #ffffff; border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px 18px;">
                    <div style="font-size: 12px; color: #64748b; font-weight: 600; text-transform: uppercase;">Earned Available</div>
                    <div style="font-size: 22px; font-weight: 700; color: #d97706;"><%= earned %> <span style="font-size: 13px; font-weight: normal; color: #64748b;">days</span></div>
                </div>
                <div style="background: #ffffff; border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px 18px;">
                    <div style="font-size: 12px; color: #64748b; font-weight: 600; text-transform: uppercase;">Emergency Available</div>
                    <div style="font-size: 22px; font-weight: 700; color: #dc2626;"><%= emergency %> <span style="font-size: 13px; font-weight: normal; color: #64748b;">days</span></div>
                </div>
            </div>

            <% if (errorMsg != null) { %>
                <div class="alert alert-danger"><%= errorMsg %></div>
            <% } %>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">Leave Application Form</div>
                </div>
                <div class="card-body">
                    <form action="<%= request.getContextPath() %>/employee/apply-leave" method="POST" class="needs-loading-state">
                        <div class="form-grid">
                            
                            <div class="form-group">
                                <label class="form-label" for="leaveType">Leave Type <span class="required">*</span></label>
                                <select id="leaveType" name="leaveType" class="form-control" required>
                                    <option value="">-- Select Leave Category --</option>
                                    <option value="CASUAL">Casual Leave (Remaining: <%= casual %> days)</option>
                                    <option value="SICK">Sick Leave (Remaining: <%= sick %> days)</option>
                                    <option value="EARNED">Earned Leave (Remaining: <%= earned %> days)</option>
                                    <option value="EMERGENCY">Emergency Leave (Remaining: <%= emergency %> days)</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="calculatedDays">Total Calculated Days</label>
                                <input type="number" id="calculatedDays" name="days" class="form-control" readonly 
                                       placeholder="Auto-calculated from dates" required min="1">
                                <small id="dateHelpText" style="font-size: 12px; margin-top: 4px; display: block; color: #64748b;">
                                    Select dates below to compute duration.
                                </small>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="fromDate">From Date <span class="required">*</span></label>
                                <input type="date" id="fromDate" name="fromDate" class="form-control" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="toDate">To Date <span class="required">*</span></label>
                                <input type="date" id="toDate" name="toDate" class="form-control" required>
                            </div>

                            <div class="form-group full-width">
                                <label class="form-label" for="reason">Reason for Leave <span class="required">*</span></label>
                                <textarea id="reason" name="reason" class="form-control" rows="4" 
                                          placeholder="Provide a specific and detailed justification for this leave request..." required></textarea>
                            </div>

                        </div>

                        <div style="display: flex; gap: 12px; justify-content: flex-end; margin-top: 10px;">
                            <a href="<%= request.getContextPath() %>/employee/dashboard" class="btn btn-secondary">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <span>🚀</span> Submit Leave Application
                            </button>
                        </div>
                    </form>
                </div>
            </div>

<jsp:include page="/common/footer.jsp" />
