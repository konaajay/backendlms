package com.lms.www.campus.Hostel;
import java.time.LocalDate;

import java.util.Map;
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
@Table(name = "hostel_complaints")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HostelComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private Long complaintId;
    
    // ================= STUDENT INFO  =================//
    
    @Column(name = "student_id", nullable =true)
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostel_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Hostel hostel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private HostelRoom room;
    
    @Column(name = "hostel_name")
    private String hostelName;

    @Column(name = "room_number")
    private String roomNumber;

    @JsonProperty("category")
    @Enumerated(EnumType.STRING)
    @Column(name = "issue_category")
    private IssueCategory issueCategory;
    
    @Enumerated(EnumType.STRING)
    private PriorityLevel priority;

    @Column(length = 1000)
    private String description;

    @Column(name = "reported_date")
    private LocalDate reportedDate;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Column(name = "admin_remarks", length = 1000)
    private String adminRemarks;

    // Manual Getters and Setters
    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Hostel getHostel() { return hostel; }
    public void setHostel(Hostel hostel) { this.hostel = hostel; }

    public HostelRoom getRoom() { return room; }
    public void setRoom(HostelRoom room) { this.room = room; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public IssueCategory getIssueCategory() { return issueCategory; }
    public void setIssueCategory(IssueCategory issueCategory) { this.issueCategory = issueCategory; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getReportedDate() { return reportedDate; }
    public void setReportedDate(LocalDate reportedDate) { this.reportedDate = reportedDate; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getAdminRemarks() { return adminRemarks; }
    @JsonProperty("remarks")
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }

    // Manual setter for nested 'student' object from frontend
    @JsonProperty("student")
    public void setStudent(Map<String, Object> student) {
        if (student != null) {
            if (student.get("id") != null) {
                this.studentId = Long.valueOf(student.get("id").toString());
            } else if (student.get("studentId") != null) {
                this.studentId = Long.valueOf(student.get("studentId").toString());
            }
            
            if (student.get("studentName") != null) {
                this.studentName = student.get("studentName").toString();
            } else if (student.get("name") != null) {
                this.studentName = student.get("name").toString();
            }
        }
    }

    // ===== ENUMS =======
    
    public enum IssueCategory {
        PLUMBING,
        ELECTRICAL,
        CLEANING,
        INTERNET,
        FURNITURE,
        OTHERS,
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