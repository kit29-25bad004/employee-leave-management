<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Employee Leave Management System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body class="login-body">

    <div class="login-card">
        <div class="login-header">
            <div class="logo-badge">🏢</div>
            <h1>Leave Management Portal</h1>
            <p>Enterprise HR Attendance & Leave Management</p>
        </div>

        <div class="login-form-area">
            <%-- Error Messages --%>
            <% 
                String errorMsg = (String) request.getAttribute("errorMessage");
                String errorParam = request.getParameter("error");
                String msgParam = request.getParameter("msg");

                if (errorMsg != null) { 
            %>
                <div class="alert alert-danger"><%= errorMsg %></div>
            <% } else if ("session_expired".equals(errorParam)) { %>
                <div class="alert alert-warning">Your session has expired. Please log in again.</div>
            <% } else if ("unauthorized".equals(errorParam)) { %>
                <div class="alert alert-danger">Access denied. Please log in with appropriate privileges.</div>
            <% } else if ("logged_out".equals(msgParam)) { %>
                <div class="alert alert-success">You have been logged out successfully.</div>
            <% } %>

            <form action="<%= request.getContextPath() %>/login" method="POST" class="needs-loading-state">
                <div class="form-group">
                    <label class="form-label" for="usernameInput">Employee ID / Username / Email <span class="required">*</span></label>
                    <input type="text" id="usernameInput" name="username" class="form-control" 
                           placeholder="e.g. ADMIN001 or admin@enterprise.com" 
                           value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>" 
                           required autofocus autocomplete="username">
                </div>

                <div class="form-group">
                    <label class="form-label" for="passwordInput">Password <span class="required">*</span></label>
                    <div class="password-toggle-wrapper">
                        <input type="password" id="passwordInput" name="password" class="form-control" 
                               placeholder="Enter your password" required autocomplete="current-password">
                        <button type="button" id="togglePasswordBtn" class="toggle-password-btn">Show</button>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 8px;">
                    Log In to Portal
                </button>
            </form>

            <%-- Demo Credentials Quick-Fill helper for faculty demonstration --%>
            <div class="demo-credentials-box">
                <div class="demo-title">
                    <span>Demo Accounts (Click to Fill)</span>
                    <span style="font-weight: normal; font-size: 10px; color: #64748b;">SHA-256 Auth</span>
                </div>
                <div class="demo-buttons-grid">
                    <button type="button" class="btn-demo" onclick="fillCredentials('ADMIN001', 'admin123')">
                        👑 Admin<br><small>ADMIN001</small>
                    </button>
                    <button type="button" class="btn-demo" onclick="fillCredentials('EMP001', 'emp123')">
                        👨‍💼 Alex<br><small>EMP001</small>
                    </button>
                    <button type="button" class="btn-demo" onclick="fillCredentials('EMP002', 'emp123')">
                        👩‍💼 Sarah<br><small>EMP002</small>
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
