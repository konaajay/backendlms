package com.lms.www.campus.model.Transport;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConductorDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long conductorId;

	@Column(nullable = false)
	private String conductorName;

	@Column(nullable = false, unique = true)
	private String contactNumber;

	@Column(nullable = false)
	private Integer experienceYears;

	@ManyToOne
	@JoinColumn(name = "route_id")
	private RouteWay route;

	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConductorVerificationStatus verificationStatus;

	public enum ConductorVerificationStatus {
		PENDING,
		VERIFIED,
		SUSPENDED,
		REJECTED
	}

	@Column(nullable = false)
	private Boolean active = true;

}
