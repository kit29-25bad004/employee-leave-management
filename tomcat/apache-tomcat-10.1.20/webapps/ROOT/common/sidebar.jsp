<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String currentRole = (String) session.getAttribute("role");
    String currentUri = request.getRequestURI();
    String ctx = request.getContextPath();
%>
<aside class="sidebar">
    <div class="sidebar-header">
        <div class="logo-badge">🏢</div>
        <div class="logo-text">
            <h2>ELMS Portal</h2>
            <span><%= "ADMIN".equalsIgnoreCase(currentRole) ? "HR Administration" : "Employee Workspace" %></span>
        </div>
    </div>

    <ul class="nav-menu">
        <% if ("ADMIN".equalsIgnoreCase(currentRole)) { %>
            <li class="nav-section-title">Administration</li>
            
            <li class="nav-item <%= currentUri.contains("/admin/dashboard") ? "active" : "" %>">
                <a href="<%= ctx %>/admin/dashboard">
                    <span class="nav-icon">📊</span>
                    <span>Admin Dashboard</span>
                </a>
            </li>

            <li class="nav-item <%= currentUri.contains("/admin/employees") ? "active" : "" %>">
                <a href="<%= ctx %>/admin/employees">
                    <span class="nav-icon">👥</span>
                    <span>Employees List</span>
                </a>
            </li>

            <li class="nav-item <%= currentUri.contains("/admin/add-employee") ? "active" : "" %>">
                <a href="<%= ctx %>/admin/add-employee">
                    <span class="nav-icon">➕</span>
                    <span>Add Employee</span>
                </a>
            </li>

            <li class="nav-section-title">Leave Governance</li>

            <li class="nav-item <%= currentUri.contains("/admin/leave-requests") ? "active" : "" %>">
                <a href="<%= ctx %>/admin/leave-requests">
                    <span class="nav-icon">⏳</span>
                    <span>Leave Requests</span>
                </a>
            </li>

            <li class="nav-item <%= currentUri.contains("/admin/leave-history") ? "active" : "" %>">
                <a href="<%= ctx %>/admin/leave-history">
                    <span class="nav-icon">📋</span>
                    <span>Leave History</span>
                </a>
            </li>

            <li class="nav-section-title">System</li>

            <li class="nav-item <%= currentUri.contains("/notifications") ? "active" : "" %>">
                <a href="<%= ctx %>/notifications">
                    <span class="nav-icon">🔔</span>
                    <span>Notifications</span>
                </a>
            </li>

        <% } else { %>
            <li class="nav-section-title">My Workspace</li>

            <li class="nav-item <%= currentUri.contains("/employee/dashboard") ? "active" : "" %>">
                <a href="<%= ctx %>/employee/dashboard">
                    <span class="nav-icon">🏠</span>
                    <span>Dashboard</span>
                </a>
            </li>

            <li class="nav-item <%= currentUri.contains("/employee/apply-leave") ? "active" : "" %>">
                <a href="<%= ctx %>/employee/apply-leave">
                    <span class="nav-icon">📝</span>
                    <span>Apply Leave</span>
                </a>
            </li>

            <li class="nav-item <%= currentUri.contains("/employee/leave-balance") ? "active" : "" %>">
                <a href="<%= ctx %>/employee/leave-balance">
                    <span class="nav-icon">💳</span>
                    <span>Leave Balance</span>
                </a>
            </li>

            <li class="nav-item <%= currentUri.contains("/employee/leave-history") ? "active" : "" %>">
                <a href="<%= ctx %>/employee/leave-history">
                    <span class="nav-icon">📜</span>
                    <span>Leave History</span>
                </a>
            </li>

            <li class="nav-section-title">Account</li>

            <li class="nav-item <%= currentUri.contains("/employee/profile") ? "active" : "" %>">
                <a href="<%= ctx %>/employee/profile">
                    <span class="nav-icon">👤</span>
                    <span>My Profile</span>
                </a>
            </li>

            <li class="nav-item <%= currentUri.contains("/notifications") ? "active" : "" %>">
                <a href="<%= ctx %>/notifications">
                    <span class="nav-icon">🔔</span>
                    <span>Notifications</span>
                </a>
            </li>
        <% } %>

        <li class="nav-item" style="margin-top: auto;">
            <a href="<%= ctx %>/logout" style="color: #f87171;">
                <span class="nav-icon">🚪</span>
                <span>Sign Out</span>
            </a>
        </li>
    </ul>
</aside>
