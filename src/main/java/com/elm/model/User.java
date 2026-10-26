package com.elm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model representing authentication credentials and system role.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String employeeId;
    private String username;
    private String password;
    private String role; // 'ADMIN' or 'EMPLOYEE'
    private Timestamp createdAt;

    public User() {
    }

    public User(String employeeId, String username, String password, String role) {
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(int id, String employeeId, String username, String password, String role, Timestamp createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.role = role;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isEmployee() {
        return "EMPLOYEE".equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
