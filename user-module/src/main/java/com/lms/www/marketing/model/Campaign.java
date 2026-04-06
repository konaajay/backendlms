package com.lms.www.marketing.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.lms.www.marketing.model.enums.CampaignChannel;
import com.lms.www.marketing.model.enums.CampaignStatus;
import com.lms.www.marketing.model.enums.TargetAudience;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(name = "campaign_name", nullable = false, length = 150)
    private String campaignName;

    @Column(name = "subject", length = 200)
    private String subject; 

    @Column(name = "campaign_type", length = 50)
    private String campaignType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "budget", nullable = false, precision = 19, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private CampaignStatus status;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 50)
    private CampaignChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience", nullable = false, length = 50)
    private TargetAudience targetAudience;

    @Column(name = "audience_filters", columnDefinition = "TEXT")
    private String audienceFilters;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @OneToMany(mappedBy = "coreCampaign", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EmailCampaign> emailCampaigns;

    // Standard Getters & Setters - Manual Restoration 🛡️🏁
    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long id) { this.campaignId = id; }
    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String name) { this.campaignName = name; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getCampaignType() { return campaignType; }
    public void setCampaignType(String type) { this.campaignType = type; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate date) { this.startDate = date; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate date) { this.endDate = date; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public CampaignStatus getStatus() { return status; }
    public void setStatus(CampaignStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CampaignChannel getChannel() { return channel; }
    public void setChannel(CampaignChannel channel) { this.channel = channel; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudience audience) { this.targetAudience = audience; }
    public String getAudienceFilters() { return audienceFilters; }
    public void setAudienceFilters(String filters) { this.audienceFilters = filters; }
    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }
    public List<EmailCampaign> getEmailCampaigns() { return emailCampaigns; }
    public void setEmailCampaigns(List<EmailCampaign> emails) { this.emailCampaigns = emails; }
}