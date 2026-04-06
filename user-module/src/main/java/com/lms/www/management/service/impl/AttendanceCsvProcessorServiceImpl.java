package com.lms.www.management.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.time.LocalDateTime;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.model.AttendanceRecord;
import com.lms.www.management.model.AttendanceUploadJob;
import com.lms.www.management.repository.AttendanceRecordRepository;
import com.lms.www.management.repository.AttendanceUploadJobRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.service.AttendanceCsvProcessorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceCsvProcessorServiceImpl
        implements AttendanceCsvProcessorService {

    private final AttendanceUploadJobRepository uploadJobRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentBatchRepository studentBatchRepository;

    @Override
    public void processUploadJob(Long uploadJobId) {

        AttendanceUploadJob job = uploadJobRepository.findById(uploadJobId)
                .orElseThrow(() -> new ResourceNotFoundException("Upload job not found"));

        File file = new File(job.getFilePath());

        if (!file.exists()) {
            throw new RuntimeException("File not found: " + job.getFilePath());
        }

        try {

            if (file.getName().toLowerCase().endsWith(".csv")) {
                processCsv(file, job);
            } else if (file.getName().toLowerCase().endsWith(".xlsx")) {
                processExcel(file, job);
            } else {
                throw new RuntimeException("Unsupported file format");
            }

            job.setStatus("PROCESSED");

        } catch (Exception e) {
            job.setStatus("FAILED");
            throw new RuntimeException("Processing failed: " + e.getMessage(), e);
        }

        uploadJobRepository.save(job);
    }

    // ===============================
    // CSV PROCESSING
    // ===============================
    private void processCsv(File file, AttendanceUploadJob job) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            boolean skipHeader = true;

            while ((line = br.readLine()) != null) {

                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                if (line.trim().isEmpty())
                    continue;

                String[] data = line.split(",");

                if (data.length < 2)
                    continue;

                String studentIdStr = data[0].trim();
                String status = data[1].trim();
                String remarks = data.length > 2 ? data[2].trim() : null;

                Long studentId = parseStudentId(studentIdStr);
                if (studentId == null || status == null)
                    continue;

                saveRecord(job, studentId, status, remarks);
            }
        }
    }

    // ===============================
    // EXCEL PROCESSING
    // ===============================
    private void processExcel(File file, AttendanceUploadJob job) throws Exception {

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                String studentIdStr = getCellValue(row.getCell(0));
                String status = getCellValue(row.getCell(1));
                String remarks = getCellValue(row.getCell(2));

                Long studentId = parseStudentId(studentIdStr);

                if (studentId == null || status == null)
                    continue;

                saveRecord(job, studentId, status, remarks);
            }
        }
    }

    // ===============================
    // SAFE CELL VALUE READER
    // ===============================
    private String getCellValue(Cell cell) {
        if (cell == null)
            return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case BLANK:
                return null;

            default:
                return null;
        }
    }

    // ===============================
    // PARSE STUDENT ID SAFELY
    // ===============================
    private Long parseStudentId(String value) {
        try {
            return value == null ? null : Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    // ===============================
    // SAVE LOGIC
    // ===============================
    private void saveRecord(
            AttendanceUploadJob job,
            Long studentId,
            String status,
            String remarks) {

        boolean enrolled = studentBatchRepository.existsByStudentIdAndBatchIdAndStatus(
                studentId,
                job.getBatchId(),
                "ACTIVE");

        if (!enrolled)
            return;

        AttendanceRecord record = new AttendanceRecord();
        record.setAttendanceSessionId(job.getSessionId());
        record.setStudentId(studentId);
        record.setStatus(status);
        record.setRemarks(remarks);
        record.setMarkedBy(job.getUploadedBy());
        record.setMarkedAt(LocalDateTime.now());
        record.setAttendanceDate(job.getAttendanceDate());
        record.setSource("FILE");

        attendanceRecordRepository.save(record);
    }
}