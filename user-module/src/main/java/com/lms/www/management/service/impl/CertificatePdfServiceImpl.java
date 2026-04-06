package com.lms.www.management.service.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.Certificate;
import com.lms.www.management.model.CertificateTemplate;
import com.lms.www.management.repository.CertificateTemplateRepository;
import com.lms.www.management.service.CertificatePdfService;
import com.lms.www.management.service.CertificateTemplateRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificatePdfServiceImpl implements CertificatePdfService {

    private final CertificateTemplateRepository templateRepository;
    private final CertificateTemplateRenderer templateRenderer;

    @Override
    public String generatePdf(
            Certificate certificate,
            String studentName,
            String eventTitle,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        try {

            // =====================================================
            // 1️⃣ Fetch Target-Specific Template First, then Fallback
            // =====================================================
            CertificateTemplate templateEntity = templateRepository
                    .findFirstByTargetTypeAndTargetIdAndIsActiveTrue(
                            TargetType.valueOf(certificate.getTargetType()),
                            certificate.getTargetId())
                    .orElseGet(() -> templateRepository
                            .findFirstByTargetTypeAndTargetIdIsNullAndIsActiveTrue(
                                    TargetType.valueOf(certificate.getTargetType()))
                            .orElse(null));

            if (certificate.getTemplateId() != null) {
                templateEntity = templateRepository
                        .findById(certificate.getTemplateId())
                        .orElse(templateEntity);
            }

            String htmlTemplate = null;

            // =====================================================
            // 2️⃣ Verification URL + QR
            // =====================================================
            String verifyUrl = "http://localhost:5151/api/certificates/public/"
                    + certificate.getVerificationToken();

            String qrBase64 = generateQrCodeBase64(verifyUrl);

            // =====================================================
            // 3️⃣ Template Data
            // =====================================================
            Map<String, Object> data = new HashMap<>();

            data.put("studentName", studentName);
            data.put("eventTitle", eventTitle);
            data.put("courseName", eventTitle);
            data.put("examTitle", eventTitle);

            data.put("certificateId", certificate.getCertificateId());

            String issuedDate = certificate.getIssuedDate()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

            data.put("issuedDate", issuedDate);
            data.put("date", issuedDate);

            data.put("verificationUrl", verifyUrl);
            data.put("qrCode", qrBase64);

            // =====================================================
            // 4️⃣ Score
            // =====================================================
            if (TargetType.valueOf(certificate.getTargetType()) == TargetType.EXAM) {

                Double score = certificate.getScore() != null
                        ? certificate.getScore()
                        : 0;

                data.put("score", score);
                data.put("scoreLine", "Score: " + score);

            } else {

                data.put("score", "");
                data.put("scoreLine", "");
            }

            // =====================================================
            // 5️⃣ Duration
            // =====================================================
            if (startDate != null && endDate != null) {

                String duration = "Duration: "
                        + startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                        + " to "
                        + endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

                data.put("durationLine", duration);

            } else {

                data.put("durationLine", "");
            }

            // =====================================================
            // 6️⃣ Template Images → Base64
            // =====================================================
            if (templateEntity != null) {

                String projectPath = new File("").getAbsolutePath();

                String logo = templateEntity.getLogoUrl();
                String signature = templateEntity.getSignatureUrl();
                String background = templateEntity.getBackgroundImageUrl();

                // LOGO
                if (logo != null && !logo.isBlank()) {

                    File file = new File(logo);

                    if (!file.isAbsolute()) {
                        file = new File(projectPath + logo);
                    }

                    String compressed = compressImage(file.getAbsolutePath());
                    logo = imageToBase64(compressed);
                }

                // SIGNATURE
                if (signature != null && !signature.isBlank()) {

                    File file = new File(signature);

                    if (!file.isAbsolute()) {
                        file = new File(projectPath + signature);
                    }

                    String compressed = compressImage(file.getAbsolutePath());
                    signature = imageToBase64(compressed);
                }

                // BACKGROUND
                if (background != null && !background.isBlank()) {

                    File file = new File(background);

                    if (!file.isAbsolute()) {
                        file = new File(projectPath + background);
                    }

                    String compressed = compressImage(file.getAbsolutePath());
                    background = imageToBase64(compressed);
                }

                data.put("logoUrl", logo);
                data.put("signatureUrl", signature);
                data.put("backgroundImageUrl", background);
            }

            // =====================================================
            // 7️⃣ Render HTML
            // =====================================================
            if (templateEntity != null
                    && templateEntity.getLayoutConfigJson() != null
                    && !templateEntity.getLayoutConfigJson().isBlank()) {

                htmlTemplate = templateRenderer.renderTemplate(
                        templateEntity.getLayoutConfigJson(),
                        data);
            }

            if (htmlTemplate == null || htmlTemplate.isBlank()) {
                throw new RuntimeException(
                        "No certificate template found for target: "
                                + certificate.getTargetType());
            }

            // =====================================================
            // 8️⃣ Certificates Folder
            // =====================================================
            File folder = new File("certificates");

            if (!folder.exists()) {
                folder.mkdirs();
            }

            String outputPath = "certificates/" + certificate.getCertificateId() + ".pdf";

            // =====================================================
            // 9️⃣ Generate PDF
            // =====================================================
            try (OutputStream os = new FileOutputStream(outputPath)) {

                PdfRendererBuilder builder = new PdfRendererBuilder();

                builder.useFastMode();

                // Match PDF page size with certificate canvas
                builder.useDefaultPageSize(1123, 794, PdfRendererBuilder.PageSizeUnits.MM);

                builder.withHtmlContent(htmlTemplate, null);

                builder.toStream(os);

                builder.run();
            }

            return outputPath;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException("PDF generation failed", e);
        }
    }

    // =====================================================
    // QR Generator
    // =====================================================
    private String generateQrCodeBase64(String text) throws Exception {

        QRCodeWriter writer = new QRCodeWriter();

        Map<com.google.zxing.EncodeHintType, Object> hints = new HashMap<>();
        hints.put(com.google.zxing.EncodeHintType.MARGIN, 0);

        var bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        com.google.zxing.client.j2se.MatrixToImageWriter
                .writeToStream(bitMatrix, "PNG", pngOutputStream);

        return "data:image/png;base64," +
                Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
    }

    // =====================================================
    // Image → Base64
    // =====================================================
    private String imageToBase64(String imagePath) {

        try {

            File file = new File(imagePath);

            if (!file.exists()) {
                return "";
            }

            byte[] fileContent = java.nio.file.Files.readAllBytes(file.toPath());

            String base64 = Base64.getEncoder().encodeToString(fileContent);

            return "data:image/png;base64," + base64;

        } catch (Exception e) {

            e.printStackTrace();
            return "";
        }
    }

    // =====================================================
    // Image Compression
    // =====================================================
    private String compressImage(String imagePath) {

        try {

            File input = new File(imagePath);

            if (!input.exists()) {
                return imagePath;
            }

            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                return imagePath;
            }

            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            // Only compress/scale down if the image is too large
            if (originalWidth <= 1200) {
                return imagePath;
            }

            int newWidth = 1200;
            int newHeight = (originalHeight * newWidth) / originalWidth;

            Image scaled = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            // Use ARGB to preserve transparency
            BufferedImage compressed = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = compressed.createGraphics();
            g2d.drawImage(scaled, 0, 0, null);
            g2d.dispose();

            File output = new File(input.getParent(), "temp_" + input.getName() + ".png");

            ImageIO.write(compressed, "png", output);

            return output.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return imagePath;
        }
    }
}