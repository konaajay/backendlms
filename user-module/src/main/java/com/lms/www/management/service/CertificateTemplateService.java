package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.CertificateTemplate;

public interface CertificateTemplateService {

    List<CertificateTemplate> getAllTemplates();

    CertificateTemplate getTemplateById(Long id);

    CertificateTemplate createTemplate(CertificateTemplate template);

    CertificateTemplate updateTemplate(Long id, CertificateTemplate template);

    void deleteTemplate(Long id);
}