package com.lms.www.campus.Hostel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


     @Entity
	@Table(name = "hostel_rooms", uniqueConstraints = {@UniqueConstraint(columnNames = {"block_id","room_number"})})
	public class HostelRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

 
    @Column(name = "room_number", nullable = false)
    private String roomNumber;
    
   
    @Enumerated(EnumType.STRING)
    @Column(name = "sharing_type", nullable = false)
    private SharingType sharingType;
    
    public enum SharingType {
        SINGLE,   // 1 bed
        DOUBLE,   // 2 beds
        TRIPLE,   // 3 beds
        QUAD      // 4 beds
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

    // Manual Getters and Setters
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public SharingType getSharingType() { return sharingType; }
    public void setSharingType(SharingType sharingType) { this.sharingType = sharingType; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public Integer getCurrentlyOccupied() { return currentlyOccupied; }
    public void setCurrentlyOccupied(Integer currentlyOccupied) { this.currentlyOccupied = currentlyOccupied; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}

