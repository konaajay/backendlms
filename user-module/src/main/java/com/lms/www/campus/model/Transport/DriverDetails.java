package com.lms.www.campus.model.Transport;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class DriverDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long driverId;

	@Column(name = "fullname", nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true)
	private String contactNumber;

	@Column(nullable = false, unique = true)
	private String licenseNumber;

	@Column(nullable = false)
	private LocalDate licenseExpiryDate;

	@Enumerated(EnumType.STRING)
	private Role role;

	public enum Role {
		DRIVER, CONDUCTOR, HELPER
	}

	@Enumerated(EnumType.STRING)
	private ExperienceCategory experienceCategory;

	public enum ExperienceCategory {
		SCHOOLBUS, HEAVYVEHICLE, LIGHTVEHICLE, BOTHSCHOOLBUSANDHEAVYVEHICLE
	}

	@Column(nullable = false)
	private Boolean backgroundVerified = false;

	@Enumerated(EnumType.STRING)
	private ShiftType shift;

	public enum ShiftType {
		MORNING, EVENING, BOTH
	}

	@Column(nullable = false)
	private Integer experienceYears;

	@Enumerated(EnumType.STRING)
	private LicenseValidityStatus licenseValidityStatus;

	public enum LicenseValidityStatus {
		VALID,
		EXPIRED,
		EXPIRINGSOON
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)

	private DriverStatus verificationStatus;

	public enum DriverStatus {
		PENDING,
		VERIFIED,
		SUSPENDED,
		REJECTED
	}

	@Column(nullable = false)
	private Boolean active = true;

	/* ================= VEHICLE ASSIGNMENT ================= */
	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	/* ================= ROUTE ASSIGNMENT ================= */
	@ManyToOne
	@JoinColumn(name = "route_id")
	private RouteWay route;

	/* ================= CONDUCTOR ASSIGNMENT ================= */
	@ManyToOne
	@JoinColumn(name = "conductor_id")
	private ConductorDetails conductor;

}
