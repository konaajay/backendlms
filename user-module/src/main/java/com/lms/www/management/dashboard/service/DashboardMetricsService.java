package com.lms.www.management.dashboard.service;

import java.util.List;

import com.lms.www.management.dashboard.dto.AttendanceSummaryDTO;
import com.lms.www.management.dashboard.dto.BatchSummaryDTO;
import com.lms.www.management.dashboard.dto.CertificateSummaryDTO;
import com.lms.www.management.dashboard.dto.ExamSummaryDTO;
import com.lms.www.management.dashboard.dto.WebinarSummaryDTO;

public interface DashboardMetricsService {

    List<BatchSummaryDTO> getBatchesForStudent(Long studentId);

    AttendanceSummaryDTO getAttendanceForStudent(Long studentId);

    List<ExamSummaryDTO> getExamsForStudent(Long studentId);

    List<CertificateSummaryDTO> getCertificatesForStudent(Long studentId);

    List<WebinarSummaryDTO> getWebinarsForStudent(Long studentId);
}