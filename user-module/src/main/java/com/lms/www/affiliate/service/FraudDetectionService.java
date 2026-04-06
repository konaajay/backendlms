package com.lms.www.affiliate.service;

public interface FraudDetectionService {
    boolean isSuspicious(String affiliateCode, String ipAddress);
}