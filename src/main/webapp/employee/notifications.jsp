<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elm.model.Notification" %>
<%@ page import="java.util.ArrayList" %>
<%
    @SuppressWarnings("unchecked")
    ArrayList<Notification> notifications = (ArrayList<Notification>) request.getAttribute("notifications");
    if (notifications == null) notifications = new ArrayList<>();
    
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    if (unreadCount == null) unreadCount = 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications - ELMS</title>
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
                    <h1 style="font-size: 22px; font-weight: 700; color: #0f172a;">Notifications</h1>
                    <p style="color: #64748b; font-size: 14px;">Updates regarding your leave submissions, approvals, and system notices</p>
                </div>
                <% if (unreadCount > 0) { %>
                    <form action="<%= request.getContextPath() %>/notifications/mark-all-read" method="POST">
                        <button type="submit" class="btn btn-secondary">
                            <span>✓✓</span> Mark All as Read
                        </button>
                    </form>
                <% } %>
            </div>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">Activity Feed (<%= unreadCount %> Unread)</div>
                </div>
                <div class="card-body">
                    <% if (notifications.isEmpty()) { %>
                        <div style="text-align: center; padding: 48px; color: #64748b;">
                            <div style="font-size: 36px; margin-bottom: 12px;">📭</div>
                            <p>You have no notifications at this time.</p>
                        </div>
                    <% } else { %>
                        <ul class="notif-list">
                            <% for (Notification n : notifications) { %>
                                <li class="notif-item <%= !n.isRead() ? "unread" : "" %>">
                                    <div class="notif-text">
                                        <div class="notif-message">
                                            <%= n.getMessage() %>
                                        </div>
                                        <div class="notif-time">
                                            <%= n.getCreatedAt() != null ? n.getCreatedAt().toString().substring(0, 16) : "" %>
                                            <% if (!n.isRead()) { %>
                                                &bull; <span style="color: #2563eb; font-weight: 600;">NEW</span>
                                            <% } %>
                                        </div>
                                    </div>
                                    <% if (!n.isRead()) { %>
                                        <div>
                                            <form action="<%= request.getContextPath() %>/notifications/mark-read" method="POST">
                                                <input type="hidden" name="notificationId" value="<%= n.getId() %>">
                                                <button type="submit" class="btn btn-secondary btn-sm" title="Mark as read">
                                                    Mark Read
                                                </button>
                                            </form>
                                        </div>
                                    <% } %>
                                </li>
                            <% } %>
                        </ul>
                    <% } %>
                </div>
            </div>

<jsp:include page="/common/footer.jsp" />
