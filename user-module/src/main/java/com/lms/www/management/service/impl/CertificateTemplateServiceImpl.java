package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.model.CertificateTemplate;
import com.lms.www.management.repository.CertificateTemplateRepository;
import com.lms.www.management.service.CertificateTemplateService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateTemplateServiceImpl implements CertificateTemplateService {

    private final CertificateTemplateRepository certificateTemplateRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CertificateTemplate> getAllTemplates() {
        return certificateTemplateRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateTemplate getTemplateById(Long id) {
        return certificateTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Certificate Template not found with id: " + id));
    }

    @Override
    @Transactional
    public CertificateTemplate createTemplate(CertificateTemplate template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        return certificateTemplateRepository.save(template);
    }

    @Override
    @Transactional
    public CertificateTemplate updateTemplate(Long id, CertificateTemplate templateDetails) {

        CertificateTemplate existingTemplate = getTemplateById(id);

        existingTemplate.setTemplateName(templateDetails.getTemplateName());
        existingTemplate.setTemplateType(templateDetails.getTemplateType());
        existingTemplate.setTargetType(templateDetails.getTargetType());
        existingTemplate.setTargetId(templateDetails.getTargetId());
        existingTemplate.setTemplateFileUrl(templateDetails.getTemplateFileUrl());
        existingTemplate.setLogoUrl(templateDetails.getLogoUrl());
        existingTemplate.setBackgroundImageUrl(templateDetails.getBackgroundImageUrl());
        existingTemplate.setSignatureUrl(templateDetails.getSignatureUrl());
        existingTemplate.setIsActive(templateDetails.getIsActive());
        existingTemplate.setLayoutConfigJson(templateDetails.getLayoutConfigJson());
        existingTemplate.setUpdatedAt(LocalDateTime.now());

        return certificateTemplateRepository.save(existingTemplate);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        CertificateTemplate template = getTemplateById(id);
        certificateTemplateRepository.delete(template);
    }
}