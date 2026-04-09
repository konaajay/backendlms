package com.lms.www.marketing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackingRequest {
    @NotBlank(message = "Tracking ID is required")
    private String trackedLinkId;
    
    @NotBlank(message = "Event type is required")
    private String eventType; // PAGE_VIEW, CLICK, LEAD
    
    @NotBlank(message = "Session ID is required")
    private String sessionId;
    
    private String metadata;
    private String source;
    
    // UTM Attribution Fields
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String page;

    public TrackingRequest() {}

    public TrackingRequest(String trackedLinkId, String eventType, String sessionId, String metadata, String source) {
        this.trackedLinkId = trackedLinkId;
        this.eventType = eventType;
        this.sessionId = sessionId;
        this.metadata = metadata;
        this.source = source;
    }

    public String getTrackedLinkId() { return trackedLinkId; }
    public void setTrackedLinkId(String id) { this.trackedLinkId = id; }
    public String getEventType() { return eventType; }
    public void setEventType(String type) { this.eventType = type; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String id) { this.sessionId = id; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getUtmSource() { return utmSource; }
    public void setUtmSource(String utmSource) { this.utmSource = utmSource; }
    public String getUtmMedium() { return utmMedium; }
    public void setUtmMedium(String utmMedium) { this.utmMedium = utmMedium; }
    public String getUtmCampaign() { return utmCampaign; }
    public void setUtmCampaign(String utmCampaign) { this.utmCampaign = utmCampaign; }
    public String getPage() { return page; }
    public void setPage(String page) { this.page = page; }

    public String getTid() {
        return trackedLinkId;
    }
}
