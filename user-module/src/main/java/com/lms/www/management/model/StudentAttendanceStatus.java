package com.lms.www.management.model;

public class StudentAttendanceStatus {

    private Long studentId;
    private int attendancePercent;
    private boolean eligible;
    private boolean atRiskByPercent;
    private boolean atRiskByAbsence;
    private boolean alertRequired;
    private String alertReason; 

    public StudentAttendanceStatus() {}

    public StudentAttendanceStatus(Long studentId, int attendancePercent, boolean eligible, boolean atRiskByPercent, boolean atRiskByAbsence, boolean alertRequired, String alertReason) {
        this.studentId = studentId;
        this.attendancePercent = attendancePercent;
        this.eligible = eligible;
        this.atRiskByPercent = atRiskByPercent;
        this.atRiskByAbsence = atRiskByAbsence;
        this.alertRequired = alertRequired;
        this.alertReason = alertReason;
    }

    // Manual Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long id) { this.studentId = id; }

    public int getAttendancePercent() { return attendancePercent; }
    public void setAttendancePercent(int percent) { this.attendancePercent = percent; }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public boolean isAtRiskByPercent() { return atRiskByPercent; }
    public void setAtRiskByPercent(boolean atRisk) { this.atRiskByPercent = atRisk; }

    public boolean isAtRiskByAbsence() { return atRiskByAbsence; }
    public void setAtRiskByAbsence(boolean atRisk) { this.atRiskByAbsence = atRisk; }

    public boolean isAlertRequired() { return alertRequired; }
    public void setAlertRequired(boolean required) { this.alertRequired = required; }

    public String getAlertReason() { return alertReason; }
    public void setAlertReason(String reason) { this.alertReason = reason; }
}
