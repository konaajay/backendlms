package com.lms.www.campus.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

// REDUNDANT - MOVED TO com.lms.www.campus.Hostel
@Data
public class HostelRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomId;

	@Column(name = "room_number", nullable = false)
	private String roomNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "sharing_type", nullable = false)
	private SharingType sharingType;

	public enum SharingType {
		SINGLE, // 1 bed
		DOUBLE, // 2 beds
		TRIPLE, // 3 beds
		QUAD // 4 beds
	}

	@Enumerated(EnumType.STRING)
	private RoomStatus status = RoomStatus.AVAILABLE;

	public enum RoomStatus {
		AVAILABLE,
		PARTIALLY_FILLED,
		FULL
	}

	// ---------- AUTO CALCULATED ----------
	@Column(name = "currently_occupied")
	private Integer currentlyOccupied;

	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

}
