package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(
    name = "attendance_config",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "batch_id"})
    }
)
public class AttendanceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // CONTEXT
    // ===============================
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    // ===============================
    // ACADEMIC THRESHOLDS
    // ===============================
    @Column(name = "exam_eligibility_percent", nullable = false)
    private Integer examEligibilityPercent;

    @Column(name = "at_risk_percent", nullable = false)
    private Integer atRiskPercent;

    // ===============================
    // ATTENDANCE TIMING RULES
    // ===============================
    @Column(name = "late_grace_minutes", nullable = false)
    private Integer lateGraceMinutes;

    @Column(name = "min_presence_minutes", nullable = false)
    private Integer minPresenceMinutes;

    @Column(name = "auto_absent_minutes", nullable = false)
    private Integer autoAbsentMinutes;

    // ===============================
    // EARLY EXIT
    // ===============================
    @Column(name = "early_exit_action", nullable = false)
    private String earlyExitAction; // MARK_PARTIAL / ABSENT

    // ===============================
    // CONTROLS
    // ===============================
    @Column(name = "allow_offline", nullable = false)
    private Boolean allowOffline;

    @Column(name = "allow_manual_override", nullable = false)
    private Boolean allowManualOverride;

    @Column(name = "require_override_reason", nullable = false)
    private Boolean requireOverrideReason;

    @Column(name = "notify_parents", nullable = false)
    private Boolean notifyParents;

    @Column(name = "one_device_per_session", nullable = false)
    private Boolean oneDevicePerSession;

    @Column(name = "log_ip_address", nullable = false)
    private Boolean logIpAddress;

    @Column(name = "strict_start", nullable = false)
    private Boolean strictStart;

    @Column(name = "qr_code_mode", nullable = false)
    private String qrCodeMode; // ALWAYS / TIME_BASED

    // ===============================
    // GRACE & ALERTS
    // ===============================
    @Column(name = "grace_period_minutes", nullable = false)
    private Integer gracePeriodMinutes;

    @Column(name = "consecutive_absence_limit", nullable = false)
    private Integer consecutiveAbsenceLimit;

    // ===============================
    // AUDIT
    // ===============================
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public AttendanceConfig() {}

    // Manual Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public Integer getExamEligibilityPercent() { return examEligibilityPercent; }
    public void setExamEligibilityPercent(Integer examEligibilityPercent) { this.examEligibilityPercent = examEligibilityPercent; }

    public Integer getAtRiskPercent() { return atRiskPercent; }
    public void setAtRiskPercent(Integer atRiskPercent) { this.atRiskPercent = atRiskPercent; }

    public Integer getLateGraceMinutes() { return lateGraceMinutes; }
    public void setLateGraceMinutes(Integer lateGraceMinutes) { this.lateGraceMinutes = lateGraceMinutes; }

    public Integer getMinPresenceMinutes() { return minPresenceMinutes; }
    public void setMinPresenceMinutes(Integer minPresenceMinutes) { this.minPresenceMinutes = minPresenceMinutes; }

    public Integer getAutoAbsentMinutes() { return autoAbsentMinutes; }
    public void setAutoAbsentMinutes(Integer autoAbsentMinutes) { this.autoAbsentMinutes = autoAbsentMinutes; }

    public String getEarlyExitAction() { return earlyExitAction; }
    public void setEarlyExitAction(String earlyExitAction) { this.earlyExitAction = earlyExitAction; }

    public Boolean getAllowOffline() { return allowOffline; }
    public void setAllowOffline(Boolean allowOffline) { this.allowOffline = allowOffline; }

    public Boolean getAllowManualOverride() { return allowManualOverride; }
    public void setAllowManualOverride(Boolean allowManualOverride) { this.allowManualOverride = allowManualOverride; }

    public Boolean getRequireOverrideReason() { return requireOverrideReason; }
    public void setRequireOverrideReason(Boolean requireOverrideReason) { this.requireOverrideReason = requireOverrideReason; }

    public Boolean getNotifyParents() { return notifyParents; }
    public void setNotifyParents(Boolean notifyParents) { this.notifyParents = notifyParents; }

    public Boolean getOneDevicePerSession() { return oneDevicePerSession; }
    public void setOneDevicePerSession(Boolean oneDevicePerSession) { this.oneDevicePerSession = oneDevicePerSession; }

    public Boolean getLogIpAddress() { return logIpAddress; }
    public void setLogIpAddress(Boolean logIpAddress) { this.logIpAddress = logIpAddress; }

    public Boolean getStrictStart() { return strictStart; }
    public void setStrictStart(Boolean strictStart) { this.strictStart = strictStart; }

    public String getQrCodeMode() { return qrCodeMode; }
    public void setQrCodeMode(String qrCodeMode) { this.qrCodeMode = qrCodeMode; }

    public Integer getGracePeriodMinutes() { return gracePeriodMinutes; }
    public void setGracePeriodMinutes(Integer gracePeriodMinutes) { this.gracePeriodMinutes = gracePeriodMinutes; }

    public Integer getConsecutiveAbsenceLimit() { return consecutiveAbsenceLimit; }
    public void setConsecutiveAbsenceLimit(Integer consecutiveAbsenceLimit) { this.consecutiveAbsenceLimit = consecutiveAbsenceLimit; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
