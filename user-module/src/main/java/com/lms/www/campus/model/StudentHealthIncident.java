package com.lms.www.campus.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

// REDUNDANT - MOVED TO com.lms.www.campus.Hostel
@Data
public class StudentHealthIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incidentId;

    // ---------------- Student Info (from token / allocation) ----------------
    @Column(nullable = true)
    private Long studentId;

    private String studentName;

    private String studentPhone;

    private String parentPhone;

    // ---------------- Hostel Mapping ----------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = true)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Hostel hostel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = true)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private HostelRoom room;

    @Column
    private String hostelName;

    @Column
    private String roomNumber;

    // ---------------- Incident Details ----------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("medicalIssueType")
    private ComplaintNature complaintNature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static Severity fromValue(String value) {
            return Severity.valueOf(value.toUpperCase());
        }
    }

    public enum ComplaintNature {
        FEVER,
        HEADACHE,
        COLD_FLU,
        STOMACH_PAIN,
        INJURY,
        BODY_PAIN,
        DIZZINESS,
        FOOD_POISONING,
        ALLERGY,
        OTHER;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static ComplaintNature fromValue(String value) {
            return ComplaintNature.valueOf(value.toUpperCase());
        }
    }

    @Enumerated(EnumType.STRING)
    private IncidentStatus currentStatus;

    public enum IncidentStatus {
        OBSERVATION,
        MEDICATED,
        HOSPITALIZED,
        RECOVERED
    }

    @JsonCreator
    public static IncidentStatus from(String value) {
        return value == null ? null : IncidentStatus.valueOf(value.toUpperCase());
    }

    @Column(nullable = false)
    private LocalDate reportedDate;

    // ---------------- Clinical Notes ----------------
    @JsonProperty("clinical_notes")
    private String clinicalNotes;

    // ---------------- Audit ----------------
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

}