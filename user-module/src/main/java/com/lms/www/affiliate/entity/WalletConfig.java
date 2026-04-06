package com.lms.www.affiliate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "default_min_payout_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal defaultMinPayoutAmount;

    @Column(name = "max_payout_amount", precision = 19, scale = 4)
    private BigDecimal maxPayoutAmount;

    @Builder.Default
    @Column(name = "student_withdrawal_enabled", nullable = false)
    private boolean studentWithdrawalEnabled = false;

    @Builder.Default
    @Column(name = "affiliate_withdrawal_enabled", nullable = false)
    private boolean affiliateWithdrawalEnabled = true;

    @Builder.Default
    @Column(name = "max_pending_payouts", nullable = false)
    private int maxPendingPayouts = 1;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
