package com.lms.www.campus.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// REDUNDANT - MOVED TO com.lms.www.campus.Hostel
public class StudentHostelFee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long feeId;

	@Column(nullable = true)
	private Long studentId;

	@Column(nullable = false)
	private String studentName;

	@Column(nullable = false)
	private Double monthlyFee;

	@Column(nullable = false)
	private Double totalFee;

	@Column(nullable = false)
	private Double amountPaid;

	@Column(nullable = false)
	private Double dueAmount;

	private LocalDate lastPaymentDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FeeStatus status;

	public enum FeeStatus {
		PAID,
		PARTIALLY_PAID,
		DUE
	}
}
