package com.lms.www.management.dashboard.dto;

public class TopicContentDTO {
    private Long contentId;
    private String contentType;
    private String contentSource;
    private String contentTitle;
    private String contentDescription;
    private String fileUrl;
    private Integer contentOrder;

    public TopicContentDTO() {}
    public TopicContentDTO(Long contentId, String contentType, String contentSource, String contentTitle, String contentDescription, String fileUrl, Integer contentOrder) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.contentSource = contentSource;
        this.contentTitle = contentTitle;
        this.contentDescription = contentDescription;
        this.fileUrl = fileUrl;
        this.contentOrder = contentOrder;
    }

    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getContentSource() { return contentSource; }
    public void setContentSource(String contentSource) { this.contentSource = contentSource; }
    public String getContentTitle() { return contentTitle; }
    public void setContentTitle(String contentTitle) { this.contentTitle = contentTitle; }
    public String getContentDescription() { return contentDescription; }
    public void setContentDescription(String contentDescription) { this.contentDescription = contentDescription; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public Integer getContentOrder() { return contentOrder; }
    public void setContentOrder(Integer contentOrder) { this.contentOrder = contentOrder; }

    public static TopicContentDTOBuilder builder() { return new TopicContentDTOBuilder(); }
    public static class TopicContentDTOBuilder {
        private Long contentId;
        private String contentType;
        private String contentSource;
        private String contentTitle;
        private String contentDescription;
        private String fileUrl;
        private Integer contentOrder;

        public TopicContentDTOBuilder contentId(Long id) { this.contentId = id; return this; }
        public TopicContentDTOBuilder contentType(String type) { this.contentType = type; return this; }
        public TopicContentDTOBuilder contentSource(String source) { this.contentSource = source; return this; }
        public TopicContentDTOBuilder contentTitle(String title) { this.contentTitle = title; return this; }
        public TopicContentDTOBuilder contentDescription(String desc) { this.contentDescription = desc; return this; }
        public TopicContentDTOBuilder fileUrl(String url) { this.fileUrl = url; return this; }
        public TopicContentDTOBuilder contentOrder(Integer order) { this.contentOrder = order; return this; }
        public TopicContentDTO build() {
            return new TopicContentDTO(contentId, contentType, contentSource, contentTitle, contentDescription, fileUrl, contentOrder);
        }
    }
}