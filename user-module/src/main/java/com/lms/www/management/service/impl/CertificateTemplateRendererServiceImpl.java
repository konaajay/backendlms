package com.lms.www.management.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.www.management.service.CertificateTemplateRenderer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateTemplateRendererServiceImpl implements CertificateTemplateRenderer {

    private final ObjectMapper objectMapper;

    @Override
    public String renderTemplate(String layoutJson, Map<String, Object> data) {

        if (layoutJson == null || layoutJson.isBlank()) {
            return "";
        }

        try {

            JsonNode rootNode = objectMapper.readTree(layoutJson);

            StringBuilder html = new StringBuilder();

            html.append("<!DOCTYPE html>");
            html.append("<html>");
            html.append("<head>");
            html.append("<style>");

            html.append("@page { margin:0; size:297mm 210mm; }");

            html.append("html, body {");
            html.append("margin:0;");
            html.append("padding:0;");
            html.append("width:297mm;");
            html.append("height:210mm;");
            html.append("overflow:hidden;");
            html.append("font-family:Arial;");
            html.append("}");

            html.append(".certificate-container{");
            html.append("position:relative;");
            html.append("width:297mm;");
            html.append("height:210mm;");
            html.append("overflow:hidden;");
            html.append("}");

            html.append("</style>");
            html.append("</head>");
            html.append("<body>");

            html.append("<div class='certificate-container'>");

            // =====================================================
            // BACKGROUND IMAGE
            // =====================================================
            if (data.get("backgroundImageUrl") != null
                    && !data.get("backgroundImageUrl").toString().isBlank()) {

                html.append("<img src='")
                        .append(data.get("backgroundImageUrl"))
                        .append("' style='position:absolute;left:0;top:0;width:100%;height:100%;z-index:0;'/>");
            }

            // =====================================================
            // RENDER JSON ELEMENTS
            // =====================================================
            JsonNode elements = rootNode.path("elements");

            if (elements.isArray()) {
                for (JsonNode element : elements) {
                    processElement(element, html, data);
                }
            }

            html.append("</div>");
            html.append("</body>");
            html.append("</html>");

            String finalHtml = html.toString();

            // =====================================================
            // GLOBAL PLACEHOLDER REPLACEMENT
            // =====================================================
            for (Map.Entry<String, Object> entry : data.entrySet()) {

                String key = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null
                        ? String.valueOf(entry.getValue())
                        : "";

                finalHtml = finalHtml.replace(key, value);
            }

            return finalHtml;

        } catch (Exception e) {

            log.error("Failed to render certificate template", e);
            throw new RuntimeException("Template rendering failed", e);
        }
    }

    private void processElement(JsonNode element, StringBuilder html, Map<String, Object> data) {

        String type = element.path("type").asText("");

        String value = element.has("value")
                ? element.path("value").asText("")
                : element.path("content").asText("");

        int x = element.path("x").asInt(0);
        int y = element.path("y").asInt(0);

        int width = element.has("width")
                ? element.path("width").asInt(150)
                : element.path("w").asInt(150);

        int height = -1;
        if (element.has("height")) {
            height = element.path("height").asInt();
        } else if (element.has("h")) {
            height = element.path("h").asInt();
        }

        double scaleX = element.has("scaleX") ? element.path("scaleX").asDouble(1.0) : 1.0;
        double scaleY = element.has("scaleY") ? element.path("scaleY").asDouble(1.0) : 1.0;

        int finalWidth = (int) (width * scaleX);
        int finalHeight = height != -1 ? (int) (height * scaleY) : -1;

        int fontSize = element.has("fontSize")
                ? element.path("fontSize").asInt(16)
                : element.path("style").path("fontSize").asInt(16);

        String color = element.has("color")
                ? element.path("color").asText("#000000")
                : element.path("style").path("color").asText("#000000");

        String textAlign = element.has("textAlign")
                ? element.path("textAlign").asText("left")
                : element.path("style").path("textAlign").asText("left");

        String fontWeight = element.has("fontWeight")
                ? element.path("fontWeight").asText("normal")
                : element.path("style").path("fontWeight").asText("normal");

        String fontStyle = element.has("fontStyle")
                ? element.path("fontStyle").asText("normal")
                : element.path("style").path("fontStyle").asText("normal");

        // =====================================================
        // SAFE PLACEHOLDER REPLACEMENT
        // =====================================================
        if (value != null && value.startsWith("{{") && value.endsWith("}}")) {

            String key = value.substring(2, value.length() - 2);

            if (data.containsKey(key)) {
                value = String.valueOf(data.get(key));
            }
        }

        switch (type) {

            // =====================================================
            // TEXT ELEMENT
            // =====================================================
            case "text":

                html.append("<div style='position:absolute;z-index:5;");
                html.append("left:").append(x).append("px;");
                html.append("top:").append(y).append("px;");
                html.append("width:").append(finalWidth).append("px;");
                if (finalHeight != -1) {
                    html.append("height:").append(finalHeight).append("px;");
                }
                html.append("font-size:").append(fontSize).append("px;");
                html.append("color:").append(color).append(";");
                html.append("text-align:").append(textAlign).append(";");
                html.append("font-weight:").append(fontWeight).append(";");
                html.append("font-style:").append(fontStyle).append(";");

                html.append("'>");
                html.append(value);
                html.append("</div>");

                break;

            // =====================================================
            // IMAGE / LOGO / SIGNATURE FROM LAYOUT
            // =====================================================
            case "image":
            case "logo":
            case "signature":

                if ("logo".equals(type) && (value == null || value.isBlank())) {

                    if (data.containsKey("logoUrl") && data.get("logoUrl") != null) {
                        value = String.valueOf(data.get("logoUrl"));
                    }

                } else if ("signature".equals(type) && (value == null || value.isBlank())) {

                    if (data.containsKey("signatureUrl") && data.get("signatureUrl") != null) {
                        value = String.valueOf(data.get("signatureUrl"));
                    }
                }

                if (value == null || value.isBlank()) {
                    break;
                }

                html.append("<img src='")
                        .append(value)
                        .append("' style='position:absolute;z-index:6;left:")
                        .append(x)
                        .append("px;top:")
                        .append(y)
                        .append("px;width:")
                        .append(finalWidth)
                        .append("px;");

                if (finalHeight != -1) {
                    html.append("height:").append(finalHeight).append("px;'/>");
                } else {
                    html.append("height:auto;'/>");
                }

                break;

            // =====================================================
            // QR CODE
            // =====================================================
            case "qr":

                String qr = data.get("qrCode") != null
                        ? String.valueOf(data.get("qrCode"))
                        : "";

                html.append("<img src='")
                        .append(qr)
                        .append("' style='position:absolute;z-index:7;left:")
                        .append(x)
                        .append("px;top:")
                        .append(y)
                        .append("px;width:")
                        .append(finalWidth)
                        .append("px;");

                if (finalHeight != -1) {
                    html.append("height:").append(finalHeight).append("px;'/>");
                } else {
                    html.append("height:auto;'/>");
                }

                break;

            default:
                log.warn("Unknown template element type: {}", type);
        }
    }
}