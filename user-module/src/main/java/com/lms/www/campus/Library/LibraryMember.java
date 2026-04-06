package com.lms.www.campus.Library;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "library_members")
@Data
public class LibraryMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    // ===== USER DETAILS =====

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String mobileNumber;

    // ===== SYSTEM FIELDS =====

    private Boolean isDeleted = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType memberType;

    public enum MemberType {
        STUDENT,
        STAFF, MEMBER, LIBRAIAN, ADMIN
    } // STUDENT / STAFF

    @Column(unique = true)
    private String studentId; // Roll No / Student ID

    private String department; // UG Engineering, MBA, etc.

    private Integer maxBooksAllowed = 3;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE,
        BLOCKED
    }

}
