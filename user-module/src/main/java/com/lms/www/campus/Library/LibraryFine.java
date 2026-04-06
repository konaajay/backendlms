package com.lms.www.campus.Library;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "library_fines")
@Data
public class LibraryFine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fine_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    private BookIssueRecord issueRecord;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fine_amount")
    private Double fineAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "paid_status")
    private Status paidStatus = Status.UNPAID;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BookIssueRecord getIssueRecord() { return issueRecord; }
    public void setIssueRecord(BookIssueRecord issueRecord) { this.issueRecord = issueRecord; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getFineAmount() { return fineAmount; }
    public void setFineAmount(Double fineAmount) { this.fineAmount = fineAmount; }
    public Status getPaidStatus() { return paidStatus; }
    public void setPaidStatus(Status paidStatus) { this.paidStatus = paidStatus; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isDeleted == null) this.isDeleted = false;
        if (this.paidStatus == null) this.paidStatus = Status.UNPAID;
    }

    public enum Status {
        PAID,
        UNPAID
    }
}
