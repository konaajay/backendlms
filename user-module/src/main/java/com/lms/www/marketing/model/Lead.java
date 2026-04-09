package com.lms.www.marketing.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leads", uniqueConstraints = @UniqueConstraint(columnNames = { "email", "batchId" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String mobile;
    private String phone;
    private Long batchId;
    private Long courseId;

    @Column(name = "course_interest")
    private String courseInterest;

    @Column(name = "referral_code")
    private String referralCode;

    // Lead source tracking
    private String source;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmContent;
    private String utmTerm;

    private String ipAddress;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
