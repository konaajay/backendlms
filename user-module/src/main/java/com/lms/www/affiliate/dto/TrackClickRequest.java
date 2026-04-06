package com.lms.www.affiliate.dto;

import lombok.Data;

@Data
public class TrackClickRequest {
    private String affiliateCode;
    private Long batchId;
    private String ipAddress;
    private String userAgent;
}
