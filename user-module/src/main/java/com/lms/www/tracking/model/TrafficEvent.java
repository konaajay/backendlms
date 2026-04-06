package com.lms.www.tracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.lms.www.marketing.model.TrackedLink;

@Entity
@Table(name = "traffic_events", indexes = {
        @Index(name = "idx_traffic_session", columnList = "session_id"),
        @Index(name = "idx_traffic_user", columnList = "user_id"),
        @Index(name = "idx_traffic_event", columnList = "event_type"),
        @Index(name = "idx_tid_type", columnList = "tracked_link_id, event_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // PAGE_VIEW, CLICK, LEAD

    @Column(name = "tracked_link_id")
    private String trackedLinkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracked_link_ref_id")
    private TrackedLink trackedLink;

    private String source;

    // UTM Attribution (Optional/Legacy support)
    @Column(name = "utm_source")
    private String utmSource;
    @Column(name = "utm_campaign")
    private String utmCampaign;
    @Column(name = "utm_medium")
    private String utmMedium;

    private String page;

    @Column(columnDefinition = "TEXT")
    private String metadataJSON;

    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    // Manual Getters/Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getTrackedLinkId() { return trackedLinkId; }
    public void setTrackedLinkId(String trackedLinkId) { this.trackedLinkId = trackedLinkId; }

    public TrackedLink getTrackedLink() { return trackedLink; }
    public void setTrackedLink(TrackedLink trackedLink) { this.trackedLink = trackedLink; }

    public String getMetadataJSON() { return metadataJSON; }
    public void setMetadataJSON(String metadataJSON) { this.metadataJSON = metadataJSON; }
}
