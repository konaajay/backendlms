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
public class StudentHostelAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationId;

    @Column(nullable = false)
    private Long studentId;

    private String studentName;
    private String studentEmail;

    @JsonProperty("fatherName")
    private String parentName;

    @JsonProperty("fatherPhone")
    private String parentPhone;

    // ---------------- Hostel Mapping ----------------

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

    private LocalDate joinDate;

    private LocalDate leaveDate;

    @Enumerated(EnumType.STRING)
    private AllocationStatus status = AllocationStatus.ACTIVE;

    public enum AllocationStatus {
        ACTIVE,
        CHECKED_OUT,
        CANCELLED
    }

    public Long getRoomId() {
        return null;
    }

}
