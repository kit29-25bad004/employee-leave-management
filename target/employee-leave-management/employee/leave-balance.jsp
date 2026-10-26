<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.LeaveBalance" %>
<%@ page import="java.util.HashMap" %>
<%
    LeaveBalance balance = (LeaveBalance) request.getAttribute("balance");
    @SuppressWarnings("unchecked")
    HashMap<String, Integer> balanceMap = (HashMap<String, Integer>) request.getAttribute("balanceMap");

    int casualRem = balance != null ? balance.getCasualLeave() : 0;
    int sickRem = balance != null ? balance.getSickLeave() : 0;
    int earnedRem = balance != null ? balance.getEarnedLeave() : 0;
    int emergencyRem = balance != null ? balance.getEmergencyLeave() : 0;

    int casualUsed = LeaveBalance.DEFAULT_CASUAL - casualRem;
    int sickUsed = LeaveBalance.DEFAULT_SICK - sickRem;
    int earnedUsed = LeaveBalance.DEFAULT_EARNED - earnedRem;
    int emergencyUsed = LeaveBalance.DEFAULT_EMERGENCY - emergencyRem;

    int totalAllowed = balance != null ? balance.getTotalAllowed() : 42;
    int totalRemaining = balance != null ? balance.getTotalRemaining() : 42;
    int totalUsed = balance != null ? balance.getTotalUsed() : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Leave Balance - ELMS</title>
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
                    <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">My Leave Balance & Quotas</h1>
                    <p style="color: #64748b; font-size: 14px;">Real-time breakdown of allocated annual leave entitlements</p>
                </div>
                <a href="<%= request.getContextPath() %>/employee/apply-leave" class="btn btn-primary">
                    <span>➕</span> Apply for Leave
                </a>
            </div>

            <!-- Overall Summary Banner -->
            <div style="background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; margin-bottom: 28px; display: flex; justify-content: space-around; align-items: center; flex-wrap: wrap; gap: 20px;">
                <div style="text-align: center;">
                    <div style="font-size: 13px; color: #64748b; font-weight: 600; text-transform: uppercase;">Total Annual Allowance</div>
                    <div style="font-size: 32px; font-weight: 800; color: #0f172a;"><%= totalAllowed %> <span style="font-size: 14px; font-weight: 600; color: #64748b;">Days</span></div>
                </div>
                <div style="width: 1px; height: 50px; background: #e2e8f0;"></div>
                <div style="text-align: center;">
                    <div style="font-size: 13px; color: #64748b; font-weight: 600; text-transform: uppercase;">Approved / Used</div>
                    <div style="font-size: 32px; font-weight: 800; color: #dc2626;"><%= totalUsed %> <span style="font-size: 14px; font-weight: 600; color: #64748b;">Days</span></div>
                </div>
                <div style="width: 1px; height: 50px; background: #e2e8f0;"></div>
                <div style="text-align: center;">
                    <div style="font-size: 13px; color: #64748b; font-weight: 600; text-transform: uppercase;">Available Remaining</div>
                    <div style="font-size: 32px; font-weight: 800; color: #16a34a;"><%= totalRemaining %> <span style="font-size: 14px; font-weight: 600; color: #64748b;">Days</span></div>
                </div>
            </div>

            <!-- 4 Visual Cards with Progress Indicators -->
            <div class="balance-grid">
                
                <!-- Casual Leave Card -->
                <div class="balance-card">
                    <div>
                        <div class="balance-header">
                            <span class="balance-title">Casual Leave</span>
                            <span class="badge" style="background: #dbeafe; color: #1d4ed8;">Annual</span>
                        </div>
                        <div class="balance-numbers">
                            <span class="balance-days-remaining"><%= casualRem %></span>
                            <span class="balance-days-total">of <%= LeaveBalance.DEFAULT_CASUAL %> Days Left</span>
                        </div>
                    </div>
                    <div>
                        <div class="progress-track" style="margin-bottom: 8px;">
                            <div class="progress-fill" style="width: <%= (casualRem * 100) / LeaveBalance.DEFAULT_CASUAL %>%; background-color: #2563eb;"></div>
                        </div>
                        <div style="display: flex; justify-content: space-between; font-size: 12px; color: #64748b;">
                            <span>Used: <%= casualUsed %> days</span>
                            <span>Remaining: <%= casualRem %> days</span>
                        </div>
                    </div>
                </div>

                <!-- Sick Leave Card -->
                <div class="balance-card">
                    <div>
                        <div class="balance-header">
                            <span class="balance-title">Sick Leave</span>
                            <span class="badge" style="background: #ccfbf1; color: #0f766e;">Medical</span>
                        </div>
                        <div class="balance-numbers">
                            <span class="balance-days-remaining" style="color: #0f766e;"><%= sickRem %></span>
                            <span class="balance-days-total">of <%= LeaveBalance.DEFAULT_SICK %> Days Left</span>
                        </div>
                    </div>
                    <div>
                        <div class="progress-track" style="margin-bottom: 8px;">
                            <div class="progress-fill" style="width: <%= (sickRem * 100) / LeaveBalance.DEFAULT_SICK %>%; background-color: #0f766e;"></div>
                        </div>
                        <div style="display: flex; justify-content: space-between; font-size: 12px; color: #64748b;">
                            <span>Used: <%= sickUsed %> days</span>
                            <span>Remaining: <%= sickRem %> days</span>
                        </div>
                    </div>
                </div>

                <!-- Earned Leave Card -->
                <div class="balance-card">
                    <div>
                        <div class="balance-header">
                            <span class="balance-title">Earned Leave</span>
                            <span class="badge" style="background: #fef3c7; color: #b45309;">Vacation</span>
                        </div>
                        <div class="balance-numbers">
                            <span class="balance-days-remaining" style="color: #d97706;"><%= earnedRem %></span>
                            <span class="balance-days-total">of <%= LeaveBalance.DEFAULT_EARNED %> Days Left</span>
                        </div>
                    </div>
                    <div>
                        <div class="progress-track" style="margin-bottom: 8px;">
                            <div class="progress-fill" style="width: <%= (earnedRem * 100) / LeaveBalance.DEFAULT_EARNED %>%; background-color: #d97706;"></div>
                        </div>
                        <div style="display: flex; justify-content: space-between; font-size: 12px; color: #64748b;">
                            <span>Used: <%= earnedUsed %> days</span>
                            <span>Remaining: <%= earnedRem %> days</span>
                        </div>
                    </div>
                </div>

                <!-- Emergency Leave Card -->
                <div class="balance-card">
                    <div>
                        <div class="balance-header">
                            <span class="balance-title">Emergency Leave</span>
                            <span class="badge" style="background: #fee2e2; color: #b91c1c;">Urgent</span>
                        </div>
                        <div class="balance-numbers">
                            <span class="balance-days-remaining" style="color: #dc2626;"><%= emergencyRem %></span>
                            <span class="balance-days-total">of <%= LeaveBalance.DEFAULT_EMERGENCY %> Days Left</span>
                        </div>
                    </div>
                    <div>
                        <div class="progress-track" style="margin-bottom: 8px;">
                            <div class="progress-fill" style="width: <%= (emergencyRem * 100) / LeaveBalance.DEFAULT_EMERGENCY %>%; background-color: #dc2626;"></div>
                        </div>
                        <div style="display: flex; justify-content: space-between; font-size: 12px; color: #64748b;">
                            <span>Used: <%= emergencyUsed %> days</span>
                            <span>Remaining: <%= emergencyRem %> days</span>
                        </div>
                    </div>
                </div>

            </div>

            <!-- Tabular Breakdown matching assignment requirement -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Detailed Leave Balance Table</div>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Leave Type</th>
                                    <th>Total Allocated</th>
                                    <th>Used / Approved</th>
                                    <th>Remaining Balance</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><strong>Casual Leave</strong></td>
                                    <td><%= LeaveBalance.DEFAULT_CASUAL %> Days</td>
                                    <td><span style="color: #b91c1c; font-weight: 600;"><%= casualUsed %> Days</span></td>
                                    <td><strong style="color: #2563eb;"><%= casualRem %> Days</strong></td>
                                    <td><span class="badge badge-approved">Active</span></td>
                                </tr>
                                <tr>
                                    <td><strong>Sick Leave</strong></td>
                                    <td><%= LeaveBalance.DEFAULT_SICK %> Days</td>
                                    <td><span style="color: #b91c1c; font-weight: 600;"><%= sickUsed %> Days</span></td>
                                    <td><strong style="color: #0f766e;"><%= sickRem %> Days</strong></td>
                                    <td><span class="badge badge-approved">Active</span></td>
                                </tr>
                                <tr>
                                    <td><strong>Earned Leave</strong></td>
                                    <td><%= LeaveBalance.DEFAULT_EARNED %> Days</td>
                                    <td><span style="color: #b91c1c; font-weight: 600;"><%= earnedUsed %> Days</span></td>
                                    <td><strong style="color: #d97706;"><%= earnedRem %> Days</strong></td>
                                    <td><span class="badge badge-approved">Active</span></td>
                                </tr>
                                <tr>
                                    <td><strong>Emergency Leave</strong></td>
                                    <td><%= LeaveBalance.DEFAULT_EMERGENCY %> Days</td>
                                    <td><span style="color: #b91c1c; font-weight: 600;"><%= emergencyUsed %> Days</span></td>
                                    <td><strong style="color: #dc2626;"><%= emergencyRem %> Days</strong></td>
                                    <td><span class="badge badge-approved">Active</span></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

<jsp:include page="/common/footer.jsp" />
