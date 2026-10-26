<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Employee Leave Management System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body class="login-body">
    <div class="login-card" style="max-width: 520px; text-align: center; padding: 40px 32px;">
        <div class="logo-badge" style="margin: 0 auto 20px; background: linear-gradient(135deg, #ef4444, #dc2626);">
            ⚠️
        </div>
        <h1 style="font-size: 24px; margin-bottom: 12px; color: #0f172a;">Something went wrong</h1>
        <p style="color: #64748b; font-size: 14px; margin-bottom: 24px;">
            The requested page could not be found or an unexpected error occurred.
        </p>

        <% if (exception != null && exception.getMessage() != null) { %>
            <div class="alert alert-danger" style="text-align: left; font-size: 13px; font-family: monospace;">
                <%= exception.getMessage() %>
            </div>
        <% } %>

        <div style="display: flex; gap: 12px; justify-content: center;">
            <a href="<%= request.getContextPath() %>/login" class="btn btn-primary">
                Return to Login
            </a>
            <button type="button" onclick="history.back()" class="btn btn-secondary">
                Go Back
            </button>
        </div>
    </div>
</body>
</html>
