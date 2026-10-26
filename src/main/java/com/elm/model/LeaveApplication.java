package com.elm.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model representing an employee's leave application.
 */
public class LeaveApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String employeeId;
    private String employeeName; // Populated via join for admin views
    private String department;   // Populated via join for admin views
    private String leaveType;    // CASUAL, SICK, EARNED, EMERGENCY
    private Date fromDate;
    private Date toDate;
    private int days;
    private String reason;
    private String status;       // PENDING, APPROVED, REJECTED
    private Timestamp appliedAt;
    private Timestamp approvedAt;

    public LeaveApplication() {
        this.status = "PENDING";
    }

    public LeaveApplication(String employeeId, String leaveType, Date fromDate, Date toDate, 
                            int days, String reason) {
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.days = days;
        this.reason = reason;
        this.status = "PENDING";
    }

    public LeaveApplication(int id, String employeeId, String leaveType, Date fromDate, Date toDate, 
                            int days, String reason, String status, Timestamp appliedAt, Timestamp approvedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.days = days;
        this.reason = reason;
        this.status = status;
        this.appliedAt = appliedAt;
        this.approvedAt = approvedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Timestamp appliedAt) {
        this.appliedAt = appliedAt;
    }

    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.status);
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(this.status);
    }

    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return "LeaveApplication{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", leaveType='" + leaveType + '\'' +
                ", days=" + days +
                ", status='" + status + '\'' +
                '}';
    }
}
