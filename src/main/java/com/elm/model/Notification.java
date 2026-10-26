package com.elm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model representing notifications for employees.
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String employeeId;
    private String message;
    private boolean read;
    private Timestamp createdAt;

    public Notification() {
    }

    public Notification(String employeeId, String message) {
        this.employeeId = employeeId;
        this.message = message;
        this.read = false;
    }

    public Notification(int id, String employeeId, String message, boolean read, Timestamp createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", read=" + read +
                '}';
    }
}
