package com.lms.www.management.model;

public class StudentAttendanceStatus {

    private Long studentId;
    private int attendancePercent;
    private boolean eligible;
    private boolean atRiskByPercent;
    private boolean atRiskByAbsence;
    private int totalSessions;
    private int attendedSessions;
    private int absentSessions;
    private boolean alertRequired;
    private String alertReason;

    public StudentAttendanceStatus() {
    }

    public StudentAttendanceStatus(Long studentId, int attendancePercent, boolean eligible, boolean atRiskByPercent,
            boolean atRiskByAbsence, int totalSessions, int attendedSessions, int absentSessions, boolean alertRequired,
            String alertReason) {
        this.studentId = studentId;
        this.attendancePercent = attendancePercent;
        this.eligible = eligible;
        this.atRiskByPercent = atRiskByPercent;
        this.atRiskByAbsence = atRiskByAbsence;
        this.totalSessions = totalSessions;
        this.attendedSessions = attendedSessions;
        this.absentSessions = absentSessions;
        this.alertRequired = alertRequired;
        this.alertReason = alertReason;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int count) {
        this.totalSessions = count;
    }

    public int getAttendedSessions() {
        return attendedSessions;
    }

    public void setAttendedSessions(int count) {
        this.attendedSessions = count;
    }

    public int getAbsentSessions() {
        return absentSessions;
    }

    public void setAbsentSessions(int count) {
        this.absentSessions = count;
    }

    // Manual Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long id) {
        this.studentId = id;
    }

    public int getAttendancePercent() {
        return attendancePercent;
    }

    public void setAttendancePercent(int percent) {
        this.attendancePercent = percent;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public boolean isAtRiskByPercent() {
        return atRiskByPercent;
    }

    public void setAtRiskByPercent(boolean atRisk) {
        this.atRiskByPercent = atRisk;
    }

    public boolean isAtRiskByAbsence() {
        return atRiskByAbsence;
    }

    public void setAtRiskByAbsence(boolean atRisk) {
        this.atRiskByAbsence = atRisk;
    }

    public boolean isAlertRequired() {
        return alertRequired;
    }

    public void setAlertRequired(boolean required) {
        this.alertRequired = required;
    }

    public String getAlertReason() {
        return alertReason;
    }

    public void setAlertReason(String reason) {
        this.alertReason = reason;
    }
}
