<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.dao.NotificationDAO" %>
<%
    String loggedInRole = (String) session.getAttribute("role");
    String loggedInName = (String) session.getAttribute("fullName");
    String loggedInEmpId = (String) session.getAttribute("employeeId");
    
    if (loggedInName == null) loggedInName = "User";
    if (loggedInRole == null) loggedInRole = "EMPLOYEE";

    int headerUnreadCount = 0;
    if (loggedInEmpId != null) {
        try {
            NotificationDAO nDAO = new NotificationDAO();
            headerUnreadCount = nDAO.getUnreadCount(loggedInEmpId);
        } catch (Exception ignored) {}
    }

    String initialLetter = loggedInName.length() > 0 ? loggedInName.substring(0, 1).toUpperCase() : "U";
%>
<header class="top-navbar">
    <div class="navbar-left">
        <button type="button" class="mobile-menu-toggle" id="mobileMenuToggle" aria-label="Toggle Navigation">
            ☰
        </button>
        <div class="page-title-badge">
            Employee Leave Management System
        </div>
    </div>

    <div class="navbar-right">
        <!-- Notification Bell -->
        <a href="<%= request.getContextPath() %>/notifications" class="notif-bell-link" title="Notifications">
            🔔
            <% if (headerUnreadCount > 0) { %>
                <span class="badge-counter"><%= headerUnreadCount %></span>
            <% } %>
        </a>

        <!-- User Badge -->
        <div class="user-profile-badge">
            <div class="avatar-circle"><%= initialLetter %></div>
            <div class="user-info-text">
                <div class="user-name"><%= loggedInName %></div>
                <div class="user-role-tag"><%= loggedInRole %> (<%= loggedInEmpId %>)</div>
            </div>
        </div>

        <!-- Logout Action Button -->
        <a href="<%= request.getContextPath() %>/logout" class="btn-logout" title="Log Out">
            <span>🚪</span>
            <span>Logout</span>
        </a>
    </div>
</header>
