package com.lms.www.affiliate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "affiliate_clicks", indexes = {
    @Index(name = "idx_click_affiliate", columnList = "affiliate_code"),
    @Index(name = "idx_click_clicked_code", columnList = "clicked_code"),
    @Index(name = "idx_batch_click", columnList = "batch_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliateClick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clicked_code", nullable = false)
    private String clickedCode; // actual code hit (link or global)

    @Column(name = "affiliate_code", nullable = false)
    private String affiliateCode; // core affiliate identity

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;

    @PrePersist
    protected void onCreate() {
        clickedAt = LocalDateTime.now();
    }
}
