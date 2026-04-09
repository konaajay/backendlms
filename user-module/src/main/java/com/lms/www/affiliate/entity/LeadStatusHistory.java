package com.lms.www.affiliate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lead_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Column(name = "old_status")
    private String oldStatus;

    @Column(name = "new_status", nullable = false)
    private String newStatus;

    @Column(name = "changed_by")
    private String changedBy;

    private String notes;

    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
