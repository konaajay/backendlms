package com.lms.www.management.dashboard.dto;

import java.time.LocalDateTime;

public class CertificateSummaryDTO {
    private Long certificateId;
    private String certificateName;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String certificateStatus;
    private String downloadUrl;

    public CertificateSummaryDTO() {}
    public CertificateSummaryDTO(Long certificateId, String certificateName, LocalDateTime issueDate, LocalDateTime expiryDate, String certificateStatus, String downloadUrl) {
        this.certificateId = certificateId;
        this.certificateName = certificateName;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.certificateStatus = certificateStatus;
        this.downloadUrl = downloadUrl;
    }

    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }
    public String getCertificateName() { return certificateName; }
    public void setCertificateName(String name) { this.certificateName = name; }
    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime date) { this.issueDate = date; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime date) { this.expiryDate = date; }
    public String getCertificateStatus() { return certificateStatus; }
    public void setCertificateStatus(String status) { this.certificateStatus = status; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String url) { this.downloadUrl = url; }

    public static CertificateSummaryDTOBuilder builder() { return new CertificateSummaryDTOBuilder(); }
    public static class CertificateSummaryDTOBuilder {
        private Long certificateId;
        private String certificateName;
        private LocalDateTime issueDate;
        private LocalDateTime expiryDate;
        private String certificateStatus;
        private String downloadUrl;

        public CertificateSummaryDTOBuilder certificateId(Long id) { this.certificateId = id; return this; }
        public CertificateSummaryDTOBuilder certificateName(String name) { this.certificateName = name; return this; }
        public CertificateSummaryDTOBuilder issueDate(LocalDateTime date) { this.issueDate = date; return this; }
        public CertificateSummaryDTOBuilder expiryDate(LocalDateTime date) { this.expiryDate = date; return this; }
        public CertificateSummaryDTOBuilder certificateStatus(String status) { this.certificateStatus = status; return this; }
        public CertificateSummaryDTOBuilder downloadUrl(String url) { this.downloadUrl = url; return this; }
        public CertificateSummaryDTO build() {
            return new CertificateSummaryDTO(certificateId, certificateName, issueDate, expiryDate, certificateStatus, downloadUrl);
        }
    }
}