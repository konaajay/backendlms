package com.lms.www.config;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.lms.www.management.model.CertificateTemplate;
import com.lms.www.management.repository.CertificateTemplateRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TemplateInitializer {

        private final CertificateTemplateRepository templateRepository;

        @PostConstruct
        public void initTemplates() {

                if (templateRepository.count() > 0) {
                        return; // already initialized
                }

                templateRepository.save(
                                CertificateTemplate.builder()
                                                .templateName("COURSE_PREMIUM")
                                                .isActive(false)
                                                .createdAt(LocalDateTime.now())
                                                .updatedAt(LocalDateTime.now())
                                                .build());

                templateRepository.save(
                                CertificateTemplate.builder()
                                                .templateName("EXAM_MODERN")
                                                .isActive(false)
                                                .createdAt(LocalDateTime.now())
                                                .updatedAt(LocalDateTime.now())
                                                .build());

                // Build a sample HTML layout for testing the new JSON layout flow
                String testLayout = "<html>" +
                                "<head><style>body { font-family: Arial; text-align: center; color: #1e3a8a; } .cert-box { border: 5px solid #1e3a8a; padding: 40px; margin: 20px; }</style></head>"
                                +
                                "<body><div class=\"cert-box\">" +
                                "<h1>CERTIFICATE OF EXCELLENCE</h1>" +
                                "<h3>This certifies that</h3>" +
                                "<h2>{{studentName}}</h2>" +
                                "<p>has successfully completed the course/exam:</p>" +
                                "<h3>{{eventTitle}}</h3>" +
                                "<p>{{scoreLine}}</p>" +
                                "<p><strong>Date: </strong>{{date}}</p>" +
                                "<br/>" +
                                "<p>Certificate ID: {{certificateId}}</p>" +
                                "<div><img src=\"data:image/png;base64,{{qrCode}}\" width=\"100\" /></div>" +
                                "</div></body></html>";

                templateRepository.save(
                                CertificateTemplate.builder()
                                                .templateName("DEFAULT")
                                                .isActive(true)
                                                .layoutConfigJson(testLayout) // Added test layout here
                                                .createdAt(LocalDateTime.now())
                                                .updatedAt(LocalDateTime.now())
                                                .build());
        }
}