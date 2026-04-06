package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {
    private Long id;
    private Long allocationId;
    private BigDecimal amount;
    private String type;
    private String mode;
    private String status;
    private String reason;
    private String rejectionReason;
    private Long approvedBy;
    private Long rejectedBy;
    private LocalDateTime requestDate;
    private LocalDateTime processedDate;
}
