package com.lms.www.affiliate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateLinkResponse {
    private String link;
    private String affiliateCode;
    private Long batchId;
}
