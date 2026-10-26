package com.elm.servlet;

import com.elm.dao.NotificationDAO;
import com.elm.model.Notification;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Handles employee notifications and marking items as read.
 */
@WebServlet(urlPatterns = {"/notifications", "/notifications/mark-read", "/notifications/mark-all-read"})
public class NotificationServlet extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String employeeId = (String) session.getAttribute("employeeId");
        ArrayList<Notification> notifications = notificationDAO.getNotificationsByEmployeeId(employeeId);
        int unreadCount = notificationDAO.getUnreadCount(employeeId);

        request.setAttribute("notifications", notifications);
        request.setAttribute("unreadCount", unreadCount);

        String role = (String) session.getAttribute("role");
        if ("ADMIN".equalsIgnoreCase(role)) {
            request.getRequestDispatcher("/admin/notifications.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/employee/notifications.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String employeeId = (String) session.getAttribute("employeeId");
        String path = request.getServletPath();

        if ("/notifications/mark-read".equals(path)) {
            String notifIdStr = request.getParameter("notificationId");
            if (notifIdStr != null) {
                try {
                    int notifId = Integer.parseInt(notifIdStr.trim());
                    notificationDAO.markAsRead(notifId, employeeId);
                } catch (NumberFormatException ignored) {}
            }
        } else if ("/notifications/mark-all-read".equals(path)) {
            notificationDAO.markAllAsRead(employeeId);
        }

        response.sendRedirect(request.getContextPath() + "/notifications");
    }
}
