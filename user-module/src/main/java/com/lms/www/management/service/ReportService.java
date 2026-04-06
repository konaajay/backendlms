package com.lms.www.management.service;

import java.util.List;
import java.util.Map;

public interface ReportService {

    Map<String, Object> getDashboardSummary();

    List<?> getStockSummary();

    List<?> getPurchaseReport();

    List<?> getIssueReport();

    List<?> getLossDamageReport();

}