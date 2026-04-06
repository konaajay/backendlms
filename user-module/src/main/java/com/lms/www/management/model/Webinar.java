package com.lms.www.management.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.www.management.enums.WebinarMode;
import com.lms.www.management.enums.WebinarStatus;
import com.lms.www.management.enums.WebinarType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "webinar")
@Getter
@Setter
@NoArgsConstructor
public class Webinar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webinar_id")
    private Long webinarId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private WebinarMode mode; // ONLINE, OFFLINE, HYBRID

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private WebinarType type; // FREE, PAID

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WebinarStatus status; // SCHEDULED, LIVE, COMPLETED, CANCELLED

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "registered_count", nullable = false)
    private Integer registeredCount = 0;

    // Online Webinar Fields
    @Column(name = "meeting_link")
    private String meetingLink;

    // Offline / Hybrid Webinar Fields
    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "venue_address")
    private String venueAddress;

    @Column(name = "venue_city")
    private String venueCity;

    @Column(name = "venue_country")
    private String venueCountry;

    @Column(name = "map_link")
    private String mapLink;

    @Column(name = "price")
    private Double price;
    
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "allow_external", nullable = false)
    private Boolean allowExternal = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = WebinarStatus.SCHEDULED;
        }
        if (this.registeredCount == null) {
            this.registeredCount = 0;
        }
        if (this.allowExternal == null) {
            this.allowExternal = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @JsonIgnore
    @OneToMany(mappedBy = "webinar", cascade = CascadeType.REMOVE)
    private List<WebinarRecording> recordings;

    public Long getWebinarId() { return webinarId; }
    public void setWebinarId(Long webinarId) { this.webinarId = webinarId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }
    public WebinarMode getMode() { return mode; }
    public void setMode(WebinarMode mode) { this.mode = mode; }
    public WebinarType getType() { return type; }
    public void setType(WebinarType type) { this.type = type; }
    public WebinarStatus getStatus() { return status; }
    public void setStatus(WebinarStatus status) { this.status = status; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    public Integer getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(Integer registeredCount) { this.registeredCount = registeredCount; }
    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public String getVenueAddress() { return venueAddress; }
    public void setVenueAddress(String venueAddress) { this.venueAddress = venueAddress; }
    public String getVenueCity() { return venueCity; }
    public void setVenueCity(String venueCity) { this.venueCity = venueCity; }
    public String getVenueCountry() { return venueCountry; }
    public void setVenueCountry(String venueCountry) { this.venueCountry = venueCountry; }
    public String getMapLink() { return mapLink; }
    public void setMapLink(String mapLink) { this.mapLink = mapLink; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Boolean getAllowExternal() { return allowExternal; }
    public void setAllowExternal(Boolean allowExternal) { this.allowExternal = allowExternal; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
