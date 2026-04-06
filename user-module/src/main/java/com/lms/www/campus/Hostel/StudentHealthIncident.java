package com.lms.www.campus.Hostel;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "student_health_incidents")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentHealthIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incident_id")
    private Long incidentId;

    // ---------------- Student Info (from token / allocation) ----------------
    @Column(name = "student_id", nullable = true)
    private Long studentId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_phone")
    private String studentPhone;

    @Column(name = "parent_phone")
    private String parentPhone;
    
    // ---------------- Hostel Mapping ----------------
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostel_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Hostel hostel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable =true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private HostelRoom room;
    
    @Column(name = "hostel_name")
    private String hostelName;

    @Column(name = "room_number")
    private String roomNumber;


    // ---------------- Incident Details ----------------
    
    @Enumerated(EnumType.STRING)
    @Column(name = "complaint_nature", nullable = false)
    @JsonProperty("medicalIssueType")
    private ComplaintNature complaintNature;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
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
    @Column(name = "current_status")
    private IncidentStatus currentStatus;
    public enum IncidentStatus {
        OBSERVATION,
        MEDICATED,
        HOSPITALIZED,
        RECOVERED;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static IncidentStatus from(String value) {
            return value == null ? null : IncidentStatus.valueOf(value.toUpperCase());
        }
    }
    @Column(name = "reported_date", nullable = false)
    private LocalDate reportedDate;

    // ---------------- Clinical Notes ----------------
    @JsonProperty("clinical_notes")
    @Column(name = "clinical_notes")
    private String clinicalNotes;

    // ---------------- Audit ----------------
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // Manual Getters and Setters
    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentPhone() { return studentPhone; }
    public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public Hostel getHostel() { return hostel; }
    public void setHostel(Hostel hostel) { this.hostel = hostel; }

    public HostelRoom getRoom() { return room; }
    public void setRoom(HostelRoom room) { this.room = room; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public ComplaintNature getComplaintNature() { return complaintNature; }
    public void setComplaintNature(ComplaintNature complaintNature) { this.complaintNature = complaintNature; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public IncidentStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(IncidentStatus currentStatus) { this.currentStatus = currentStatus; }

    public LocalDate getReportedDate() { return reportedDate; }
    public void setReportedDate(LocalDate reportedDate) { this.reportedDate = reportedDate; }

    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.currentStatus = IncidentStatus.from(status);
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.clinicalNotes = remarks;
    }
}