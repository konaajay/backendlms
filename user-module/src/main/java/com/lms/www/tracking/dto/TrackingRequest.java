package com.lms.www.tracking.dto;

import lombok.Data;

@Data
public class TrackingRequest {
    private String trackedLinkId;
    private String eventType;
    private String sessionId;
    private String metadata;
    private String source;
    private String page;

    // Manual Getters
    public String getTrackedLinkId() { return trackedLinkId; }
    public String getEventType() { return eventType; }
    public String getSessionId() { return sessionId; }
    public String getMetadata() { return metadata; }
    public String getSource() { return source; }
    public String getPage() { return page; }
}
