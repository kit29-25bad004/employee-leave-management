package com.elm.model;

import java.io.Serializable;

/**
 * Model representing leave quota balance for an employee.
 */
public class LeaveBalance implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_CASUAL = 12;
    public static final int DEFAULT_SICK = 10;
    public static final int DEFAULT_EARNED = 15;
    public static final int DEFAULT_EMERGENCY = 5;

    private int id;
    private String employeeId;
    private int casualLeave;
    private int sickLeave;
    private int earnedLeave;
    private int emergencyLeave;

    public LeaveBalance() {
        this.casualLeave = DEFAULT_CASUAL;
        this.sickLeave = DEFAULT_SICK;
        this.earnedLeave = DEFAULT_EARNED;
        this.emergencyLeave = DEFAULT_EMERGENCY;
    }

    public LeaveBalance(String employeeId, int casualLeave, int sickLeave, int earnedLeave, int emergencyLeave) {
        this.employeeId = employeeId;
        this.casualLeave = casualLeave;
        this.sickLeave = sickLeave;
        this.earnedLeave = earnedLeave;
        this.emergencyLeave = emergencyLeave;
    }

    public LeaveBalance(int id, String employeeId, int casualLeave, int sickLeave, int earnedLeave, int emergencyLeave) {
        this.id = id;
        this.employeeId = employeeId;
        this.casualLeave = casualLeave;
        this.sickLeave = sickLeave;
        this.earnedLeave = earnedLeave;
        this.emergencyLeave = emergencyLeave;
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

    public int getCasualLeave() {
        return casualLeave;
    }

    public void setCasualLeave(int casualLeave) {
        this.casualLeave = casualLeave;
    }

    public int getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(int sickLeave) {
        this.sickLeave = sickLeave;
    }

    public int getEarnedLeave() {
        return earnedLeave;
    }

    public void setEarnedLeave(int earnedLeave) {
        this.earnedLeave = earnedLeave;
    }

    public int getEmergencyLeave() {
        return emergencyLeave;
    }

    public void setEmergencyLeave(int emergencyLeave) {
        this.emergencyLeave = emergencyLeave;
    }

    public int getTotalRemaining() {
        return casualLeave + sickLeave + earnedLeave + emergencyLeave;
    }

    public int getTotalAllowed() {
        return DEFAULT_CASUAL + DEFAULT_SICK + DEFAULT_EARNED + DEFAULT_EMERGENCY;
    }

    public int getTotalUsed() {
        return getTotalAllowed() - getTotalRemaining();
    }

    public int getBalanceForType(String leaveType) {
        if (leaveType == null) return 0;
        return switch (leaveType.toUpperCase().trim()) {
            case "CASUAL" -> casualLeave;
            case "SICK" -> sickLeave;
            case "EARNED" -> earnedLeave;
            case "EMERGENCY" -> emergencyLeave;
            default -> 0;
        };
    }

    public boolean deduct(String leaveType, int days) {
        if (days <= 0) return false;
        if (leaveType == null) return false;
        switch (leaveType.toUpperCase().trim()) {
            case "CASUAL":
                if (casualLeave >= days) {
                    casualLeave -= days;
                    return true;
                }
                return false;
            case "SICK":
                if (sickLeave >= days) {
                    sickLeave -= days;
                    return true;
                }
                return false;
            case "EARNED":
                if (earnedLeave >= days) {
                    earnedLeave -= days;
                    return true;
                }
                return false;
            case "EMERGENCY":
                if (emergencyLeave >= days) {
                    emergencyLeave -= days;
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "LeaveBalance{" +
                "employeeId='" + employeeId + '\'' +
                ", casual=" + casualLeave +
                ", sick=" + sickLeave +
                ", earned=" + earnedLeave +
                ", emergency=" + emergencyLeave +
                '}';
    }
}
