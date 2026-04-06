package com.lms.www.campus.Library;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "library_settings")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LibrarySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    @JsonProperty("id")
    private Long settingId;

    @Column(name = "max_books")
    private Integer maxBooks;

    @Column(name = "issue_duration_days")
    private Integer issueDurationDays;

    @Column(name = "reservation_duration_days")
    private Integer reservationDurationDays;

    @Column(name = "member_role") // Added for role-based settings (Student/Faculty)
    private String memberRole;

    @JsonManagedReference
    @OneToMany(mappedBy = "librarySettings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FineSlab> fineSlabs = new ArrayList<>();

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("id")
    public Long getSettingId() { return settingId; }
    @JsonProperty("id")
    public void setSettingId(Long settingId) { this.settingId = settingId; }
    public Integer getMaxBooks() { return maxBooks; }
    public void setMaxBooks(Integer maxBooks) { this.maxBooks = maxBooks; }
    public Integer getIssueDurationDays() { return issueDurationDays; }
    public void setIssueDurationDays(Integer days) { this.issueDurationDays = days; }
    public Integer getReservationDurationDays() { return reservationDurationDays; }
    public void setReservationDurationDays(Integer days) { this.reservationDurationDays = days; }
    public String getMemberRole() { return memberRole; }
    public void setMemberRole(String role) { this.memberRole = role; }
    public List<FineSlab> getFineSlabs() { return fineSlabs; }
    public void setFineSlabs(List<FineSlab> slabs) { this.fineSlabs = slabs; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean deleted) { this.isDeleted = deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt) { this.updatedAt = dt; }

    // alias for service safety
    public Long getId() {
        return this.settingId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
