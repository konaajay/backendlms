package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.model.CertificateTemplate;
import com.lms.www.management.service.CertificateTemplateService;
import com.lms.www.management.util.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/certificate-templates")
@RequiredArgsConstructor
public class CertificateTemplateController {

    private final CertificateTemplateService certificateTemplateService;

    // =========================================================
    // VIEW ALL TEMPLATES
    // =========================================================
    @GetMapping
    @PreAuthorize("hasAuthority('CERTIFICATE_TEMPLATE_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<CertificateTemplate>> getAllTemplates() {
        return ResponseEntity.ok(
                certificateTemplateService.getAllTemplates());
    }

    // =========================================================
    // VIEW TEMPLATE BY ID
    // =========================================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CERTIFICATE_TEMPLATE_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<CertificateTemplate> getTemplateById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                certificateTemplateService.getTemplateById(id));
    }

    // =========================================================
    // CREATE TEMPLATE
    // =========================================================
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('CERTIFICATE_TEMPLATE_CREATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<CertificateTemplate> createTemplateWithImages(

            @RequestParam String templateName,
            @RequestParam String templateType,
            @RequestParam com.lms.www.management.enums.TargetType targetType,
            @RequestParam(required = false) Long targetId,
            @RequestParam Boolean isActive,
            @RequestParam String layoutConfigJson,

            @RequestParam(required = false) MultipartFile templateFile,
            @RequestParam(required = false) MultipartFile logoFile,
            @RequestParam(required = false) MultipartFile backgroundFile,
            @RequestParam(required = false) MultipartFile signatureFile

    ) {

        try {

            String templateFileUrl = null;
            String logoUrl = null;
            String backgroundUrl = null;
            String signatureUrl = null;

            if (templateFile != null && !templateFile.isEmpty()) {
                templateFileUrl = FileUploadUtil.saveTemplateFile(templateFile);
            }

            if (logoFile != null && !logoFile.isEmpty()) {
                logoUrl = FileUploadUtil.saveTemplateFile(logoFile);
            }

            if (backgroundFile != null && !backgroundFile.isEmpty()) {
                backgroundUrl = FileUploadUtil.saveTemplateFile(backgroundFile);
            }

            if (signatureFile != null && !signatureFile.isEmpty()) {
                signatureUrl = FileUploadUtil.saveTemplateFile(signatureFile);
            }

            CertificateTemplate template = CertificateTemplate.builder()
                    .templateName(templateName)
                    .templateType(templateType)
                    .targetType(targetType != null ? targetType.name() : null)
                    .targetId(targetId)
                    .templateFileUrl(templateFileUrl)
                    .logoUrl(logoUrl)
                    .backgroundImageUrl(backgroundUrl)
                    .signatureUrl(signatureUrl)
                    .layoutConfigJson(layoutConfigJson)
                    .isActive(isActive)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(certificateTemplateService.createTemplate(template));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Template upload failed", e);
        }
    }

    // =========================================================
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('CERTIFICATE_TEMPLATE_UPDATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<CertificateTemplate> updateTemplateWithImages(

            @PathVariable Long id,

            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String templateType,
            @RequestParam(required = false) com.lms.www.management.enums.TargetType targetType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String layoutConfigJson,

            @RequestParam(required = false) MultipartFile templateFile,
            @RequestParam(required = false) MultipartFile logoFile,
            @RequestParam(required = false) MultipartFile backgroundFile,
            @RequestParam(required = false) MultipartFile signatureFile

    ) {

        try {

            CertificateTemplate existing = certificateTemplateService.getTemplateById(id);

            String templateFileUrl = existing.getTemplateFileUrl();
            String logoUrl = existing.getLogoUrl();
            String backgroundUrl = existing.getBackgroundImageUrl();
            String signatureUrl = existing.getSignatureUrl();

            if (templateFile != null && !templateFile.isEmpty()) {
                templateFileUrl = FileUploadUtil.saveTemplateFile(templateFile);
            }

            if (logoFile != null && !logoFile.isEmpty()) {
                logoUrl = FileUploadUtil.saveTemplateFile(logoFile);
            }

            if (backgroundFile != null && !backgroundFile.isEmpty()) {
                backgroundUrl = FileUploadUtil.saveTemplateFile(backgroundFile);
            }

            if (signatureFile != null && !signatureFile.isEmpty()) {
                signatureUrl = FileUploadUtil.saveTemplateFile(signatureFile);
            }

            CertificateTemplate template = CertificateTemplate.builder()
                    .id(id)
                    .templateName(templateName != null ? templateName : existing.getTemplateName())
                    .templateType(templateType != null ? templateType : existing.getTemplateType())
                    .targetType(targetType != null ? targetType.name() : existing.getTargetType())
                    .targetId(targetId != null ? targetId : existing.getTargetId())
                    .templateFileUrl(templateFileUrl)
                    .logoUrl(logoUrl)
                    .backgroundImageUrl(backgroundUrl)
                    .signatureUrl(signatureUrl)
                    .layoutConfigJson(layoutConfigJson != null ? layoutConfigJson : existing.getLayoutConfigJson())
                    .isActive(isActive != null ? isActive : existing.getIsActive())
                    .build();

            return ResponseEntity.ok(
                    certificateTemplateService.updateTemplate(id, template));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Template update failed", e);
        }
    }

    // =========================================================
    // DELETE TEMPLATE
    // =========================================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CERTIFICATE_TEMPLATE_DELETE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {

        certificateTemplateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}