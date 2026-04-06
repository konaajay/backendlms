package com.lms.www.management.dashboard.dto;

import java.util.List;

public class StudentDashboardDTO {
    private StudentInfoDTO studentInfo;
    private ProgressSummaryDTO progressSummary;
    private List<CourseProgressDTO> courses;
    private List<BatchSummaryDTO> batches;
    private AttendanceSummaryDTO attendanceSummary;
    private List<ExamSummaryDTO> exams;
    private List<CertificateSummaryDTO> certificates;
    private List<WebinarSummaryDTO> webinars;

    public StudentDashboardDTO() {}
    public StudentDashboardDTO(StudentInfoDTO studentInfo, ProgressSummaryDTO progressSummary, List<CourseProgressDTO> courses, List<BatchSummaryDTO> batches, AttendanceSummaryDTO attendanceSummary, List<ExamSummaryDTO> exams, List<CertificateSummaryDTO> certificates, List<WebinarSummaryDTO> webinars) {
        this.studentInfo = studentInfo;
        this.progressSummary = progressSummary;
        this.courses = courses;
        this.batches = batches;
        this.attendanceSummary = attendanceSummary;
        this.exams = exams;
        this.certificates = certificates;
        this.webinars = webinars;
    }

    public StudentInfoDTO getStudentInfo() { return studentInfo; }
    public void setStudentInfo(StudentInfoDTO studentInfo) { this.studentInfo = studentInfo; }
    public ProgressSummaryDTO getProgressSummary() { return progressSummary; }
    public void setProgressSummary(ProgressSummaryDTO progressSummary) { this.progressSummary = progressSummary; }
    public List<CourseProgressDTO> getCourses() { return courses; }
    public void setCourses(List<CourseProgressDTO> courses) { this.courses = courses; }
    public List<BatchSummaryDTO> getBatches() { return batches; }
    public void setBatches(List<BatchSummaryDTO> batches) { this.batches = batches; }
    public AttendanceSummaryDTO getAttendanceSummary() { return attendanceSummary; }
    public void setAttendanceSummary(AttendanceSummaryDTO attendanceSummary) { this.attendanceSummary = attendanceSummary; }
    public List<ExamSummaryDTO> getExams() { return exams; }
    public void setExams(List<ExamSummaryDTO> exams) { this.exams = exams; }
    public List<CertificateSummaryDTO> getCertificates() { return certificates; }
    public void setCertificates(List<CertificateSummaryDTO> certificates) { this.certificates = certificates; }
    public List<WebinarSummaryDTO> getWebinars() { return webinars; }
    public void setWebinars(List<WebinarSummaryDTO> webinars) { this.webinars = webinars; }

    public static StudentDashboardDTOBuilder builder() { return new StudentDashboardDTOBuilder(); }
    public static class StudentDashboardDTOBuilder {
        private StudentInfoDTO studentInfo;
        private ProgressSummaryDTO progressSummary;
        private List<CourseProgressDTO> courses;
        private List<BatchSummaryDTO> batches;
        private AttendanceSummaryDTO attendanceSummary;
        private List<ExamSummaryDTO> exams;
        private List<CertificateSummaryDTO> certificates;
        private List<WebinarSummaryDTO> webinars;

        public StudentDashboardDTOBuilder studentInfo(StudentInfoDTO info) { this.studentInfo = info; return this; }
        public StudentDashboardDTOBuilder progressSummary(ProgressSummaryDTO summary) { this.progressSummary = summary; return this; }
        public StudentDashboardDTOBuilder courses(List<CourseProgressDTO> courses) { this.courses = courses; return this; }
        public StudentDashboardDTOBuilder batches(List<BatchSummaryDTO> batches) { this.batches = batches; return this; }
        public StudentDashboardDTOBuilder attendanceSummary(AttendanceSummaryDTO summary) { this.attendanceSummary = summary; return this; }
        public StudentDashboardDTOBuilder exams(List<ExamSummaryDTO> exams) { this.exams = exams; return this; }
        public StudentDashboardDTOBuilder certificates(List<CertificateSummaryDTO> certificates) { this.certificates = certificates; return this; }
        public StudentDashboardDTOBuilder webinars(List<WebinarSummaryDTO> webinars) { this.webinars = webinars; return this; }
        public StudentDashboardDTO build() {
            return new StudentDashboardDTO(studentInfo, progressSummary, courses, batches, attendanceSummary, exams, certificates, webinars);
        }
    }
}