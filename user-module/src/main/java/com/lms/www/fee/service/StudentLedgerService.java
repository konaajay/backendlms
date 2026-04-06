package com.lms.www.fee.service;

import com.lms.www.fee.dto.StudentFeeDashboardResponse;
import com.lms.www.fee.dto.StudentLedgerResponse;
import java.util.Map;

public interface StudentLedgerService {
    StudentLedgerResponse getLedger(Long studentId);
    StudentFeeDashboardResponse getDashboard(Long studentId);
    Map<String, String> createOrder(Long installmentId, Long studentId);
}
