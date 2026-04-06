package com.lms.www.management.service;

import java.util.Map;

public interface CertificateTemplateRenderer {

    String renderTemplate(String layoutJson, Map<String, Object> data);
}
