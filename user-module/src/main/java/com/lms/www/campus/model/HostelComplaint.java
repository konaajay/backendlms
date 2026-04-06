package com.lms.www.campus.model;

import java.time.LocalDate;

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
public class HostelComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    // ================= STUDENT INFO =================//

    @Column(name = "student_id", nullable = true)
    private Long studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    /*
     * @Column(name = "student_email", nullable = false) private String
     * studentEmail;
     * 
     * @Column(name = "student_phone", nullable = false) private String
     * studentPhone;
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Hostel hostel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private HostelRoom room;

    @Column
    private String hostelName;

    @Column
    private String roomNumber;

    @JsonProperty("category")
    @Enumerated(EnumType.STRING)
    private IssueCategory issueCategory;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priority;

    @Column(length = 1000)
    private String description;

    private LocalDate reportedDate;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Column(length = 1000)
    private String adminRemarks;

    // ===== ENUMS =======

    public enum IssueCategory {
        PLUMBING,
        ELECTRICAL,
        CLEANING,
        INTERNET,
        FURNITURE,
        OTHER
    }

    public enum PriorityLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum ComplaintStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
        CLOSED
    }

}