package com.lms.www.management.dashboard.dto;

import java.time.LocalDate;
import java.util.List;

public class BatchSummaryDTO {
    private Long batchId;
    private String batchName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String instructorInfo;
    private List<SessionProgressDTO> sessions;

    public BatchSummaryDTO() {}
    public BatchSummaryDTO(Long batchId, String batchName, LocalDate startDate, LocalDate endDate, String instructorInfo, List<SessionProgressDTO> sessions) {
        this.batchId = batchId;
        this.batchName = batchName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructorInfo = instructorInfo;
        this.sessions = sessions;
    }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getInstructorInfo() { return instructorInfo; }
    public void setInstructorInfo(String info) { this.instructorInfo = info; }
    public List<SessionProgressDTO> getSessions() { return sessions; }
    public void setSessions(List<SessionProgressDTO> sessions) { this.sessions = sessions; }

    public static BatchSummaryDTOBuilder builder() { return new BatchSummaryDTOBuilder(); }
    public static class BatchSummaryDTOBuilder {
        private Long batchId;
        private String batchName;
        private LocalDate startDate;
        private LocalDate endDate;
        private String instructorInfo;
        private List<SessionProgressDTO> sessions;

        public BatchSummaryDTOBuilder batchId(Long id) { this.batchId = id; return this; }
        public BatchSummaryDTOBuilder batchName(String name) { this.batchName = name; return this; }
        public BatchSummaryDTOBuilder startDate(LocalDate date) { this.startDate = date; return this; }
        public BatchSummaryDTOBuilder endDate(LocalDate date) { this.endDate = date; return this; }
        public BatchSummaryDTOBuilder instructorInfo(String info) { this.instructorInfo = info; return this; }
        public BatchSummaryDTOBuilder sessions(List<SessionProgressDTO> sessions) { this.sessions = sessions; return this; }
        public BatchSummaryDTO build() {
            return new BatchSummaryDTO(batchId, batchName, startDate, endDate, instructorInfo, sessions);
        }
    }
}