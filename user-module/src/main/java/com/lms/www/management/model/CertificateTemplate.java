package com.lms.www.management.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.lms.www.management.enums.TargetType;

@Entity
@Table(name = "certificate_template")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Consistent with service expectation

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Column(name = "template_type")
    private String templateType; // Restoration

    @Column(name = "target_type")
    private String targetType; // Restoration

    @Column(name = "target_id")
    private Long targetId; // Restoration

    @Column(name = "template_file_url")
    private String templateFileUrl; // Restoration

    @Transient
    private String contentHtml;

    @Transient
    private String bgImageUrl;

    @Column(name = "background_image_url")
    private String backgroundImageUrl; // Restoration alias

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "signature_url")
    private String signatureUrl;

    @Transient
    private String fontFamily;

    @Transient
    private String primaryColor;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Transient
    private String cssStyles;

    @Transient
    private String layoutJson;

    @Column(name = "layout_config_json", columnDefinition = "JSON")
    private String layoutConfigJson; // Restoration alias for Builder

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Custom Builder to handle TargetType enum
    public static class CertificateTemplateBuilder {
        public CertificateTemplateBuilder targetType(TargetType targetType) {
            this.targetType = targetType != null ? targetType.name() : null;
            return this;
        }
        
        public CertificateTemplateBuilder targetType(String targetType) {
            this.targetType = targetType;
            return this;
        }
    }
}