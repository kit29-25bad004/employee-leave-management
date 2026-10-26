-- ====================================================================
-- Employee Leave Management System (ELMS) Database Schema
-- Database: employee_leave_db
-- Compatibility: MySQL 8.0+
-- ====================================================================

DROP DATABASE IF EXISTS employee_leave_db;
CREATE DATABASE employee_leave_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE employee_leave_db;

-- --------------------------------------------------------------------
-- 1. Table: employees
-- --------------------------------------------------------------------
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(50) NOT NULL,
    designation VARCHAR(50) NOT NULL,
    date_of_joining DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_emp_id (employee_id),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 2. Table: users
-- Passwords stored as SHA-256 hex strings (64 chars)
-- --------------------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'ADMIN' or 'EMPLOYEE'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_employee FOREIGN KEY (employee_id) 
        REFERENCES employees(employee_id) 
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 3. Table: leave_balance
-- Tracks available leave quotas for each leave type
-- --------------------------------------------------------------------
CREATE TABLE leave_balance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL UNIQUE,
    casual_leave INT NOT NULL DEFAULT 12,
    sick_leave INT NOT NULL DEFAULT 10,
    earned_leave INT NOT NULL DEFAULT 15,
    emergency_leave INT NOT NULL DEFAULT 5,
    CONSTRAINT fk_balance_employee FOREIGN KEY (employee_id) 
        REFERENCES employees(employee_id) 
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 4. Table: leave_applications
-- Stores all employee leave requests and their statuses
-- --------------------------------------------------------------------
CREATE TABLE leave_applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    leave_type VARCHAR(30) NOT NULL, -- 'CASUAL', 'SICK', 'EARNED', 'EMERGENCY'
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    days INT NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'APPROVED', 'REJECTED'
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT fk_leaves_employee FOREIGN KEY (employee_id) 
        REFERENCES employees(employee_id) 
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_leave_status (status),
    INDEX idx_leave_emp (employee_id),
    INDEX idx_leave_dates (from_date, to_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 5. Table: notifications
-- Tracks notifications sent to employees regarding their leaves
-- --------------------------------------------------------------------
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_employee FOREIGN KEY (employee_id) 
        REFERENCES employees(employee_id) 
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_notif_emp (employee_id),
    INDEX idx_notif_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- DEMO INITIAL DATA SEEDING
-- Passwords:
-- 'admin123' -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- 'emp123'   -> e03d3ec8d5035f8721f5dc64546e59ed790dbcb3b7b598fe57057ccd7b683b00
-- ====================================================================

-- 1. Insert Employees
INSERT INTO employees (employee_id, full_name, email, phone, department, designation, date_of_joining) VALUES
('ADMIN001', 'System Administrator', 'admin@enterprise.com', '9876543210', 'Human Resources', 'HR Lead Director', '2022-01-15'),
('EMP001', 'Alex Johnson', 'alex.johnson@enterprise.com', '9876543211', 'Engineering', 'Senior Software Engineer', '2023-03-01'),
('EMP002', 'Sarah Williams', 'sarah.williams@enterprise.com', '9876543212', 'Marketing', 'Product Marketing Specialist', '2023-06-15');

-- 2. Insert Users (Auth credentials with SHA-256)
INSERT INTO users (employee_id, username, password, role) VALUES
('ADMIN001', 'ADMIN001', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'),
('EMP001', 'EMP001', 'e03d3ec8d5035f8721f5dc64546e59ed790dbcb3b7b598fe57057ccd7b683b00', 'EMPLOYEE'),
('EMP002', 'EMP002', 'e03d3ec8d5035f8721f5dc64546e59ed790dbcb3b7b598fe57057ccd7b683b00', 'EMPLOYEE');

-- 3. Insert Initial Leave Balances
-- Default allowances: Casual: 12, Sick: 10, Earned: 15, Emergency: 5
INSERT INTO leave_balance (employee_id, casual_leave, sick_leave, earned_leave, emergency_leave) VALUES
('ADMIN001', 12, 10, 15, 5),
('EMP001', 10, 9, 15, 5),   -- Used 2 Casual and 1 Sick in initial history
('EMP002', 12, 10, 15, 5);

-- 4. Insert Demo Leave Applications
INSERT INTO leave_applications (employee_id, leave_type, from_date, to_date, days, reason, status, applied_at, approved_at) VALUES
('EMP001', 'CASUAL', DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 19 DAY), 2, 'Attending family wedding ceremony', 'APPROVED', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
('EMP001', 'SICK', DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), 1, 'Doctor appointment for seasonal flu checkup', 'APPROVED', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
('EMP001', 'EARNED', DATE_ADD(CURRENT_DATE, INTERVAL 15 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 18 DAY), 4, 'Annual planned vacation with family', 'PENDING', NOW(), NULL),
('EMP002', 'CASUAL', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 6 DAY), 2, 'Personal urgent family commitment', 'PENDING', NOW(), NULL);

-- 5. Insert Demo Notifications
INSERT INTO notifications (employee_id, message, is_read, created_at) VALUES
('EMP001', 'Welcome to the Employee Leave Management Portal! Your account is active.', TRUE, DATE_SUB(NOW(), INTERVAL 30 DAY)),
('EMP001', 'Your CASUAL leave application for 2 days has been APPROVED by HR.', TRUE, DATE_SUB(NOW(), INTERVAL 20 DAY)),
('EMP001', 'Your SICK leave application for 1 day has been APPROVED by HR.', FALSE, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('EMP001', 'Your EARNED leave application for 4 days has been submitted and is currently PENDING review.', FALSE, NOW()),
('EMP002', 'Welcome to the Employee Leave Management Portal! Your account is active.', TRUE, DATE_SUB(NOW(), INTERVAL 15 DAY)),
('EMP002', 'Your CASUAL leave application for 2 days has been submitted and is currently PENDING review.', FALSE, NOW());
