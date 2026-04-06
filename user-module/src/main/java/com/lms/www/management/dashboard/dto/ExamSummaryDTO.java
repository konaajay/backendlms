package com.lms.www.management.dashboard.dto;

import java.time.LocalDateTime;

public class ExamSummaryDTO {
    private Long examId;
    private String examName;
    private String attemptStatus;
    private Double score;
    private String passFailStatus;
    private LocalDateTime attemptDate;

    public ExamSummaryDTO() {}
    public ExamSummaryDTO(Long examId, String examName, String attemptStatus, Double score, String passFailStatus, LocalDateTime attemptDate) {
        this.examId = examId;
        this.examName = examName;
        this.attemptStatus = attemptStatus;
        this.score = score;
        this.passFailStatus = passFailStatus;
        this.attemptDate = attemptDate;
    }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public String getExamName() { return examName; }
    public void setExamName(String name) { this.examName = name; }
    public String getAttemptStatus() { return attemptStatus; }
    public void setAttemptStatus(String status) { this.attemptStatus = status; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getPassFailStatus() { return passFailStatus; }
    public void setPassFailStatus(String status) { this.passFailStatus = status; }
    public LocalDateTime getAttemptDate() { return attemptDate; }
    public void setAttemptDate(LocalDateTime date) { this.attemptDate = date; }

    public static ExamSummaryDTOBuilder builder() { return new ExamSummaryDTOBuilder(); }
    public static class ExamSummaryDTOBuilder {
        private Long examId;
        private String examName;
        private String attemptStatus;
        private Double score;
        private String passFailStatus;
        private LocalDateTime attemptDate;

        public ExamSummaryDTOBuilder examId(Long id) { this.examId = id; return this; }
        public ExamSummaryDTOBuilder examName(String name) { this.examName = name; return this; }
        public ExamSummaryDTOBuilder attemptStatus(String status) { this.attemptStatus = status; return this; }
        public ExamSummaryDTOBuilder score(Double score) { this.score = score; return this; }
        public ExamSummaryDTOBuilder passFailStatus(String status) { this.passFailStatus = status; return this; }
        public ExamSummaryDTOBuilder attemptDate(LocalDateTime date) { this.attemptDate = date; return this; }
        public ExamSummaryDTO build() {
            return new ExamSummaryDTO(examId, examName, attemptStatus, score, passFailStatus, attemptDate);
        }
    }
}