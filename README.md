# Employee Leave Management System (ELMS)

A complete, enterprise-grade, database-connected **Employee Leave Management System** web application developed with **Java 21**, **Jakarta Servlets 6.0**, **JSP**, **JDBC**, and **MySQL**. Built following the strict **MVC (Model-View-Controller) architecture**, this project provides a full-featured HR leave tracking portal with separate portals for **Employees** and **Administrators**.

---

## 🌟 Key Features

### 🔐 1. Authentication & Role-Based Security
- **Database Authentication:** Authenticates credentials directly against the MySQL database.
- **SHA-256 Password Hashing:** Passwords are never stored in plain text; authenticated via a reusable `PasswordUtil.java` class.
- **Role-Based Routing:**
  - `ADMIN` is automatically routed to `/admin/dashboard`
  - `EMPLOYEE` is automatically routed to `/employee/dashboard`
- **Session Security & Filters:**
  - `AuthFilter` intercepts protected URLs (`/admin/*`, `/employee/*`), prevents unauthorized cross-access, and redirects unauthenticated visits to login.
  - Safe session termination via `session.invalidate()` on logout.
- **Show/Hide Password Toggle & Demo Auto-Fill:** Interactive quick buttons to test demo accounts immediately.

### 👨‍💼 2. Employee Portal
- **Dynamic Dashboard:** Real-time metrics loaded from MySQL:
  - Total Applied Leaves
  - Approved Leaves
  - Pending Leaves
  - Rejected Leaves
  - Remaining Leave Quota (in days)
  - Recent Applications Table with status badges
- **Apply for Leave:**
  - Categories: `CASUAL`, `SICK`, `EARNED`, `EMERGENCY`
  - Dynamic JavaScript day calculation between From Date and To Date
  - Server-side date range validation and date overlap detection
  - Leave balance validation (prevents applying for more days than available)
  - Submissions enter `PENDING` state without deducting balance prematurely
- **Leave Balance Overview:**
  - Visual cards with progress indicators showing used vs. remaining quota
  - Full tabular breakdown of all leave types
- **Leave History & Multi-Filter Search:**
  - Dynamic filtering by **Status** (`PENDING`, `APPROVED`, `REJECTED`), **Leave Type**, **Date Range**, and **Keyword Search**
- **Employee Profile Self-Service:**
  - Displays personnel details: Employee ID, Department, Designation, Date of Joining
  - Editable: Full Name, Email, Phone (Employee ID and Role are strictly protected)
- **Notifications Feed:**
  - Live notification alerts for leave approvals, rejections, submissions, and onboarding
  - Unread badge counter in header
  - "Mark as Read" and "Mark All as Read" functionality

### 👑 3. Admin Portal
- **Executive Admin Dashboard:**
  - Live statistics: Total Employees, Total Requests, Pending Requests, Approved Requests, Rejected Requests
  - Quick action buttons to approve or reject pending requests directly
- **Employee Directory (Full CRUD):**
  - **Create:** Adds new employee with login credentials (SHA-256) and initializes leave quotas (12 Casual, 10 Sick, 15 Earned, 5 Emergency) inside an **atomic database transaction**.
  - **Read:** Search employees by Employee ID, Name, or Department.
  - **Update:** Edit employee details (name, email, phone, department, designation, date of joining).
  - **Delete:** Safe delete with confirmation modal; removes related user credentials, balances, leaves, and notifications within a database transaction.
- **Leave Approval Governance:**
  - Dedicated Pending Approvals Queue (`/admin/leave-requests`)
  - **Approve Action:**
    - Checks status is `PENDING`
    - Atomically updates status to `APPROVED`
    - Deducts the appropriate leave quota from `leave_balance`
    - Creates an approval notification for the employee
    - Commits transaction (or rolls back on error)
  - **Reject Action:**
    - Updates status to `REJECTED`
    - Does **not** deduct leave balance
    - Dispatches a rejection notification to the employee
- **All Company Leave Records (Audit Log):**
  - Multi-parameter filter across all employees, departments, leave types, statuses, and date ranges.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 (LTS) |
| **Web Framework** | Jakarta Servlet 6.0 (`jakarta.servlet.*`), Jakarta Server Pages (JSP) 3.1 |
| **Tag Libraries** | Jakarta JSTL 3.0 |
| **Database** | MySQL 8.0+ |
| **Data Access** | JDBC (PreparedStatements, Connection, Transactions) |
| **Build Tool** | Apache Maven 3.9+ |
| **Web Server** | Apache Tomcat 10.1+ (Jakarta EE 10 compliant) |
| **Frontend** | HTML5, CSS3 (Enterprise Theme), Vanilla JavaScript |

---

## 📂 Project Structure

```text
employee-leave-management/
├── pom.xml                                      # Maven build configuration (Java 21, Jakarta 6.0, MySQL)
├── README.md                                    # Documentation and setup guide
├── database/
│   └── schema.sql                               # Executable MySQL schema with demo accounts and data
└── src/
    └── main/
        ├── java/
        │   └── com/elm/
        │       ├── model/                       # OOP Domain Models
        │       │   ├── Employee.java
        │       │   ├── User.java
        │       │   ├── LeaveApplication.java
        │       │   ├── LeaveBalance.java
        │       │   └── Notification.java
        │       ├── dao/                         # Data Access Objects (JDBC & SQL queries)
        │       │   ├── UserDAO.java
        │       │   ├── EmployeeDAO.java
        │       │   ├── LeaveDAO.java
        │       │   ├── LeaveBalanceDAO.java
        │       │   └── NotificationDAO.java
        │       ├── service/                     # Business Logic & Transactions
        │       │   ├── AuthService.java
        │       │   ├── EmployeeService.java
        │       │   └── LeaveService.java
        │       ├── servlet/                     # Jakarta Controllers & HTTP Handlers
        │       │   ├── AuthFilter.java          # Role-based security filter
        │       │   ├── LoginServlet.java
        │       │   ├── LogoutServlet.java
        │       │   ├── DashboardServlet.java
        │       │   ├── ProfileServlet.java
        │       │   ├── LeaveServlet.java
        │       │   ├── AdminLeaveServlet.java
        │       │   ├── EmployeeServlet.java
        │       │   └── NotificationServlet.java
        │       └── util/                        # Reusable Utilities
        │           ├── DBConnection.java        # Centralized JDBC connection manager
        │           └── PasswordUtil.java        # Reusable SHA-256 hashing utility
        └── webapp/
            ├── css/
            │   └── styles.css                   # Enterprise responsive stylesheet
            ├── js/
            │   └── main.js                      # Client calculation, modals & toggles
            ├── common/
            │   ├── header.jsp                   # Universal top navigation & bell icon
            │   ├── sidebar.jsp                  # Role-aware sidebar
            │   └── footer.jsp                   # Universal footer
            ├── index.jsp                        # Root redirect to /login
            ├── login.jsp                        # Modern login with demo quick-fill
            ├── error.jsp                        # User-friendly error handler
            ├── employee/
            │   ├── dashboard.jsp                # Dynamic employee dashboard
            │   ├── apply-leave.jsp              # Leave application form
            │   ├── leave-balance.jsp            # Visual balance indicators
            │   ├── leave-history.jsp            # Filterable leave history table
            │   ├── profile.jsp                  # Employee profile self-service
            │   └── notifications.jsp            # Employee notifications
            ├── admin/
            │   ├── dashboard.jsp                # Executive metrics & quick approvals
            │   ├── employees.jsp                # Employee directory & search
            │   ├── add-employee.jsp             # New employee registration
            │   ├── edit-employee.jsp            # Edit employee details
            │   ├── leave-requests.jsp           # Pending approvals queue
            │   ├── leave-history.jsp            # Company-wide leave history & audit
            │   └── notifications.jsp            # Admin notifications
            └── WEB-INF/
                └── web.xml                      # Jakarta EE 10 deployment descriptor
```

---

## 🗄️ Database Setup (MySQL)

### 1. Ensure MySQL is Installed and Running
Make sure the MySQL service is started (via MySQL Workbench, XAMPP, or Windows Services).

### 2. Execute SQL Script
Open a terminal / command prompt or MySQL Workbench and execute the provided `database/schema.sql`:

```bash
mysql -u root -p < database/schema.sql
```
*(Enter your MySQL root password when prompted).*

This will automatically:
- Create the database `employee_leave_db`
- Create all 5 tables (`employees`, `users`, `leave_balance`, `leave_applications`, `notifications`) with foreign keys and cascade rules
- Insert all pre-configured demo records with SHA-256 hashed credentials

---

## ⚙️ Database Password Configuration

The database connection is managed in one central location:
[`DBConnection.java`](file:///src/main/java/com/elm/util/DBConnection.java)

By default:
```java
private static final String DEFAULT_HOST = "localhost";
private static final String DEFAULT_PORT = "3306";
private static final String DEFAULT_DB = "employee_leave_db";
private static final String DEFAULT_USER = "root";
public static final String DEFAULT_PASSWORD = "root"; // <-- CHANGE HERE IF NEEDED
```

### Options to configure your password:
1. **Option A (Direct in code):** Open `src/main/java/com/elm/util/DBConnection.java` and change `DEFAULT_PASSWORD` to your actual MySQL root password.
2. **Option B (Environment Variable):** Set the Windows environment variable:
   ```powershell
   [System.Environment]::SetEnvironmentVariable('MYSQL_PASSWORD', 'your_actual_password', 'User')
   ```
3. **Option C (JVM argument):** Pass `-Ddb.password=your_actual_password` when starting Tomcat.

---

## 🔨 How to Build Using Maven

Ensure Java 21 and Maven are installed.

In the project root directory (`employee-leave-management/`), run:

```bash
mvn clean package
```

### Generated WAR File:
Upon a successful build, the WAR file will be generated at:
```text
target/employee-leave-management.war
```

---

## 🚀 Running on Port 8080

The application has been configured with Apache Tomcat 10.1 to run on the standard web port **`8080`**:

### One-Click Launch:
Simply run the included batch script from the project folder:
```cmd
start-server.bat
```

To stop the server:
```cmd
stop-server.bat
```

### Access URLs:
- **Root URL:** `http://localhost:8080/`
- **Context URL:** `http://localhost:8080/employee-leave-management/`

---

## 🔑 Demo Login Credentials

The SQL script creates the following demo accounts out of the box:

| Role | Username / Employee ID | Plain Password | Stored SHA-256 Hash |
|---|---|---|---|
| **Admin** | `ADMIN001` | `admin123` | `240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9` |
| **Employee 1** | `EMP001` | `emp123` | `e03d3ec8d5035f8721f5dc64546e59ed790dbcb3b7b598fe57057ccd7b683b00` |
| **Employee 2** | `EMP002` | `emp123` | `e03d3ec8d5035f8721f5dc64546e59ed790dbcb3b7b598fe57057ccd7b683b00` |

*Tip: On the login page, you can also click any of the "Demo Accounts" quick-fill buttons to auto-populate the fields.*

---

## 🧪 Demonstration Workflow Guide

To demonstrate the complete end-to-end integration to your evaluator:

1. **Step 1 - Employee Leaves Submission:**
   - Log in as `EMP001` with `emp123`.
   - Go to **Apply Leave**.
   - Select `CASUAL`, choose next week's dates (e.g. 2 days), enter reason: *"Attending family conference"*.
   - Click **Submit**. Notice:
     - Record is saved in MySQL as `PENDING`.
     - Balance is **not** deducted yet.
     - View in **Leave History** showing `PENDING`.
   - Click **Logout**.

2. **Step 2 - Admin Review & Approval:**
   - Log in as `ADMIN001` with `admin123`.
   - Go to **Leave Requests**.
   - See the new pending application from Alex Johnson (`EMP001`).
   - Click the green **✓ Approve** button.
   - Database transaction executes:
     - Status updates to `APPROVED`.
     - `casual_leave` quota in `leave_balance` is reduced by 2.
     - Notification is generated.
   - Click **Logout**.

3. **Step 3 - Employee Balance Verification:**
   - Log in again as `EMP001` with `emp123`.
   - See unread notification bell 🔔: *"Great news! Your CASUAL leave application has been APPROVED"*.
   - Go to **Leave Balance**: Casual leave reflects the deduction.
   - Go to **Leave History**: Status is `APPROVED`.

4. **Step 4 - Employee Directory Management:**
   - Log in as `ADMIN001`.
   - Go to **Add Employee**, fill in details for `EMP003`, password `password123`.
   - Submit: Transaction creates employee, credentials, balance, and notification.
   - Search for `EMP003` in **Employees List**.
   - Test **Edit** and **Delete** with confirmation dialog.

---

## 🩺 Troubleshooting

1. **"Access denied for user 'root'@'localhost'"**
   - Check your password in `DBConnection.java` or set the `MYSQL_PASSWORD` environment variable.
2. **"Unknown database 'employee_leave_db'"**
   - Ensure you ran `database/schema.sql` into MySQL.
3. **"404 Not Found" on Tomcat**
   - Ensure you are using Tomcat 10.1+ (Tomcat 9 or older uses `javax.servlet` which will not run Jakarta EE 10 WARs).
   - Ensure the context path in the browser matches the WAR name: `http://localhost:8080/employee-leave-management/`.
4. **"Port 8080 already in use"**
   - Change Tomcat's HTTP port in `conf/server.xml` from `8080` to `8081` or terminate the conflicting process.
