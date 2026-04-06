package com.lms.www.management.dashboard.dto;

public class SessionContentDTO {
    private Long sessionContentId;
    private String title;
    private String description;
    private String contentType;
    private String fileUrl;
    private String status;
    private Integer totalDuration;

    public SessionContentDTO() {}
    public SessionContentDTO(Long sessionContentId, String title, String description, String contentType, String fileUrl, String status, Integer totalDuration) {
        this.sessionContentId = sessionContentId;
        this.title = title;
        this.description = description;
        this.contentType = contentType;
        this.fileUrl = fileUrl;
        this.status = status;
        this.totalDuration = totalDuration;
    }

    public Long getSessionContentId() { return sessionContentId; }
    public void setSessionContentId(Long sessionContentId) { this.sessionContentId = sessionContentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getTotalDuration() { return totalDuration; }
    public void setTotalDuration(Integer totalDuration) { this.totalDuration = totalDuration; }

    public static SessionContentDTOBuilder builder() { return new SessionContentDTOBuilder(); }
    public static class SessionContentDTOBuilder {
        private Long sessionContentId;
        private String title;
        private String description;
        private String contentType;
        private String fileUrl;
        private String status;
        private Integer totalDuration;

        public SessionContentDTOBuilder sessionContentId(Long id) { this.sessionContentId = id; return this; }
        public SessionContentDTOBuilder title(String title) { this.title = title; return this; }
        public SessionContentDTOBuilder description(String desc) { this.description = desc; return this; }
        public SessionContentDTOBuilder contentType(String type) { this.contentType = type; return this; }
        public SessionContentDTOBuilder fileUrl(String url) { this.fileUrl = url; return this; }
        public SessionContentDTOBuilder status(String status) { this.status = status; return this; }
        public SessionContentDTOBuilder totalDuration(Integer duration) { this.totalDuration = duration; return this; }
        public SessionContentDTO build() {
            return new SessionContentDTO(sessionContentId, title, description, contentType, fileUrl, status, totalDuration);
        }
    }
}