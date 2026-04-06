package com.lms.www.campus.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

// REDUNDANT - MOVED TO com.lms.www.campus.Hostel
@Data
public class StudentVisitEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long visitId;

	// ================= STUDENT INFO (FROM TOKEN) =================

	@Column(nullable = true)
	private Long studentId;

	@Column(name = "student_name", nullable = false)
	private String studentName;

	// ================= VISITOR INFO =================
	@JsonProperty("parentName")
	@Column(nullable = false)
	private String visitorName;

	@JsonProperty("relation")
	@Column(name = "relationship")
	private String relationship;

	@JsonProperty("contactNumber")
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

}
