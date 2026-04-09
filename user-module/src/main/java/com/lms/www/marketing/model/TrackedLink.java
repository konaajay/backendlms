package com.lms.www.marketing.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracked_links")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackedLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tracked_link_id", nullable = false)
    private String trackedLinkId;

    @Column(name = "landing_slug")
    private String landingSlug;

    @Column(name = "source")
    private String source;

    @Column(name = "medium")
    private String medium;

    @Column(name = "campaign")
    private String campaign;

    @Column(name = "generated_link")
    private String generatedLink;

    @Column(name = "ad_budget")
    private BigDecimal adBudget;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

}
