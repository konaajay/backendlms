package com.lms.www.management.dashboard.dto;

import java.time.LocalDateTime;

public class WebinarSummaryDTO {
    private Long webinarId;
    private String webinarTitle;
    private String registrationStatus;
    private String attendanceStatus;
    private LocalDateTime webinarDate;
    private String recordingUrl;

    public WebinarSummaryDTO() {}
    public WebinarSummaryDTO(Long webinarId, String webinarTitle, String registrationStatus, String attendanceStatus, LocalDateTime webinarDate, String recordingUrl) {
        this.webinarId = webinarId;
        this.webinarTitle = webinarTitle;
        this.registrationStatus = registrationStatus;
        this.attendanceStatus = attendanceStatus;
        this.webinarDate = webinarDate;
        this.recordingUrl = recordingUrl;
    }

    public Long getWebinarId() { return webinarId; }
    public void setWebinarId(Long webinarId) { this.webinarId = webinarId; }
    public String getWebinarTitle() { return webinarTitle; }
    public void setWebinarTitle(String title) { this.webinarTitle = title; }
    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String status) { this.registrationStatus = status; }
    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String status) { this.attendanceStatus = status; }
    public LocalDateTime getWebinarDate() { return webinarDate; }
    public void setWebinarDate(LocalDateTime date) { this.webinarDate = date; }
    public String getRecordingUrl() { return recordingUrl; }
    public void setRecordingUrl(String url) { this.recordingUrl = url; }

    public static WebinarSummaryDTOBuilder builder() { return new WebinarSummaryDTOBuilder(); }
    public static class WebinarSummaryDTOBuilder {
        private Long webinarId;
        private String webinarTitle;
        private String registrationStatus;
        private String attendanceStatus;
        private LocalDateTime webinarDate;
        private String recordingUrl;

        public WebinarSummaryDTOBuilder webinarId(Long id) { this.webinarId = id; return this; }
        public WebinarSummaryDTOBuilder webinarTitle(String title) { this.webinarTitle = title; return this; }
        public WebinarSummaryDTOBuilder registrationStatus(String status) { this.registrationStatus = status; return this; }
        public WebinarSummaryDTOBuilder attendanceStatus(String status) { this.attendanceStatus = status; return this; }
        public WebinarSummaryDTOBuilder webinarDate(LocalDateTime date) { this.webinarDate = date; return this; }
        public WebinarSummaryDTOBuilder recordingUrl(String url) { this.recordingUrl = url; return this; }
        public WebinarSummaryDTO build() {
            return new WebinarSummaryDTO(webinarId, webinarTitle, registrationStatus, attendanceStatus, webinarDate, recordingUrl);
        }
    }
}