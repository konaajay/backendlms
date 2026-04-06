package com.lms.www.campus.Hostel;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "student_visit_entry")
public class StudentVisitEntry {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "visit_id")
	    private Long visitId;

	    // ================= STUDENT INFO (FROM TOKEN) =================
	    
	    @Column(name = "student_id", nullable = true)
	    private Long studentId;

	    @Column(name = "student_name", nullable = false)
	    private String studentName;

	    // ================= VISITOR INFO =================
	    @JsonProperty("parentName")
	    @Column(name = "visitor_name", nullable = false)
	    private String visitorName;

	    @JsonProperty("relation")
	    @Column(name = "relationship")
	    private String relationship;

	    @JsonProperty("contactNumber")
	    @Column(name = "visitor_contact")
	    private String visitorContact;

	    // ================= VISIT DETAILS =================
	    @Column(name = "visit_date", nullable = false)
	    private LocalDate visitDate;

	    @Column(name = "visit_time", nullable = false)
	    private LocalTime visitTime;

	    @JsonProperty("purpose")
	    @Column(name = "purpose_of_visit")
	    private String purposeOfVisit;
	    // ================= STATUS & AUDIT =================
	    
	    @JsonProperty("visitStatus")
	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private VisitStatus status;



	    @Column(name = "created_at", updatable = false)
	    private LocalDate createdAt = LocalDate.now();

	    // ================= ENUMS =================
	    public enum Relationship {
	        FATHER,
	        MOTHER,
	        GUARDIAN,
	        RELATIVE,
	        OTHER
	    }

	    public enum VisitStatus {
	        SCHEDULED,
	        CHECKED_IN,
	        CHECKED_OUT,
	        CANCELLED
	    }

        // Manual Getters and Setters
        public Long getVisitId() { return visitId; }
        public void setVisitId(Long visitId) { this.visitId = visitId; }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public String getVisitorName() { return visitorName; }
        public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }

        public String getVisitorContact() { return visitorContact; }
        public void setVisitorContact(String visitorContact) { this.visitorContact = visitorContact; }

        public LocalDate getVisitDate() { return visitDate; }
        public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }

        public LocalTime getVisitTime() { return visitTime; }
        public void setVisitTime(LocalTime visitTime) { this.visitTime = visitTime; }

        public String getPurposeOfVisit() { return purposeOfVisit; }
        public void setPurposeOfVisit(String purposeOfVisit) { this.purposeOfVisit = purposeOfVisit; }

        public VisitStatus getStatus() { return status; }
        public void setStatus(VisitStatus status) { this.status = status; }

        public LocalDate getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
