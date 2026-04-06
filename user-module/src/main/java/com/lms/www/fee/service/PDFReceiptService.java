package com.lms.www.fee.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PDFReceiptService {

    public byte[] generateReceiptPDF(String receiptNumber, String studentName, StudentFeeAllocation allocation,
            BigDecimal amount,
            String currency, String mode, String txRef, LocalDateTime date) {

        log.info("Generating Real PDF for receipt: {}", receiptNumber);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A5); // A5 is standard for receipts
            PdfWriter.getInstance(document, out);

            document.open();

            // Fonts
            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
            com.lowagie.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            com.lowagie.text.Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            com.lowagie.text.Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            // Header Section
            Paragraph header = new Paragraph("PAYMENT RECEIPT", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20);
            document.add(header);

            // Main Info Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            addCell(table, "Receipt No:", boldFont);
            addCell(table, receiptNumber, bodyFont);

            addCell(table, "Student Name:", boldFont);
            addCell(table, studentName, bodyFont);

            addCell(table, "Allocation ID:", boldFont);
            addCell(table, allocation.getId().toString(), bodyFont);

            addCell(table, "Course:", boldFont);
            addCell(table, allocation.getCourseName(), bodyFont);

            if (allocation.getAdmissionFeeAmount() != null
                    && allocation.getAdmissionFeeAmount().compareTo(BigDecimal.ZERO) > 0) {
                addCell(table, "Admission Fee:", boldFont);
                addCell(table, currency + " " + allocation.getAdmissionFeeAmount().toString(), bodyFont);
            }

            addCell(table, "Payment Date:", boldFont);
            addCell(table, date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")), bodyFont);

            addCell(table, "Payment Mode:", boldFont);
            addCell(table, mode, bodyFont);

            addCell(table, "Transaction Ref:", boldFont);
            addCell(table, txRef, bodyFont);

            document.add(table);

            // Amount Section
            Paragraph amountPara = new Paragraph("\nAmount Paid: " + currency + " " + amount,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY));
            amountPara.setAlignment(Element.ALIGN_RIGHT);
            amountPara.setSpacingBefore(20);
            document.add(amountPara);

            // Footer
            Paragraph footer = new Paragraph(
                    "\n\nThank you for your payment.\nClass X 360 – Learning Management System", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF receipt: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    private void addCell(PdfPTable table, String text, com.lowagie.text.Font font) {
        PdfPCell cell = new PdfPCell(new com.lowagie.text.Phrase(text, font));
        cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
