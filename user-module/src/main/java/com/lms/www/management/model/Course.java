package com.lms.www.management.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "courses")
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name", nullable = false, unique = true)
    private String courseName;

    private String description;
    private String duration;

    @Column(name = "tools_covered")
    private String toolsCovered;

    @Column(name = "course_fee")
    private Double courseFee;

    @Column(name = "certificate_provided")
    private Boolean certificateProvided;

    private String status = "ACTIVE";

    @Column(name = "show_validity")
    private Boolean showValidity;

    @Column(name = "validity_in_days")
    private Integer validityInDays;

    @Column(name = "allow_offline_mobile")
    private Boolean allowOfflineMobile;

    @Column(name = "allow_bookmark")
    private Boolean allowBookmark;

    @Column(name = "course_image_url")
    private String courseImageUrl;

    @Column(name = "share_code", unique = true)
    private String shareCode;

    @Column(name = "share_enabled")
    private Boolean shareEnabled = true;

    @Column(name = "certificate_template_id")
    private Long certificateTemplateId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "course_title")
    private String courseTitle;

    @Column(name = "course_description")
    private String courseDescription;

    // ❌ REMOVED (not in DB)
    // private Boolean enableContentAccess;

    // ❌ TRANSIENT (not stored)
    @Transient
    private String shareLink;

    @Transient
    private List<Topic> topics;

    // ===============================
    // LIFECYCLE
    // ===============================
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}