package com.lms.www.campus.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

// REDUNDANT - MOVED TO com.lms.www.campus.Hostel
@Data
public class Hostel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hostelId;

    @Column(unique = true, nullable = false)
    private String hostelName;

    @Enumerated(EnumType.STRING)
    private HostelType hostelType;

    public enum HostelType {
        MEN,
        WOMEN,
        COLIVING
    }

    private Integer totalBlocks = 0;

    private Integer totalRooms = 0;

    private String wardenName;

    private String contactNumber;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    private Boolean isDeleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
