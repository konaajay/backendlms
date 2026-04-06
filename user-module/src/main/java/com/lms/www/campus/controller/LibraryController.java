package com.lms.www.campus.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.campus.Library.BookBarcode;
import com.lms.www.campus.Library.BookCategory;
import com.lms.www.campus.Library.BookIssueRecord;
import com.lms.www.campus.Library.BookReservation;
import com.lms.www.campus.Library.Books;
import com.lms.www.campus.Library.LibraryFine;
import com.lms.www.campus.Library.LibrarySettings;
import com.lms.www.campus.service.LibraryService;
import com.lms.www.common.util.QRCodeUtil;

import lombok.RequiredArgsConstructor;

@RestController("campusLibraryController")
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LibraryController {

    private final LibraryService service;

    // ================= CATEGORY =================

    @PostMapping("/categories")
    // @PreAuthorize("hasAuthority('BOOK_CATEGORY_CREATE')")
    public ResponseEntity<BookCategory> createCategory(@RequestBody BookCategory category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createCategory(category));
    }

    @GetMapping("/categories")
    // @PreAuthorize("hasAuthority('BOOK_CATEGORY_VIEW')") // Removed to allow all
    // authenticated users to view categories
    public List<BookCategory> getCategories() {
        return service.getCategories();
    }

    @PutMapping("/categories/{id}")
    // @PreAuthorize("hasAuthority('BOOK_CATEGORY_UPDATE')")
    public BookCategory updateCategory(
            @PathVariable Long id,
            @RequestBody BookCategory data) {
        return service.updateCategory(id, data);
    }

    @DeleteMapping("/categories/{id}")
    // @PreAuthorize("hasAuthority('BOOK_CATEGORY_DELETE')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ================= BOOK =================

    @PostMapping("/books")
    public ResponseEntity<Books> createBook(@RequestBody Books book) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createBook(book));
    }

    @GetMapping("/books")
    public List<Books> getBooks() {
        return service.getBooks();
    }

    @GetMapping("/books/category/{id}")
    public List<Books> getBooksByCategory(@PathVariable Long id) {
        return service.getBooksByCategory(id);
    }

    @PutMapping("/books/{id}")
    public Books updateBook(
            @PathVariable Long id,
            @RequestBody Books data) {
        return service.updateBook(id, data);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        service.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // ================= ISSUE / RETURN =================

    @PostMapping("/books/issue")
    public BookIssueRecord issueBook(
            @RequestParam Long bookId,
            @RequestParam Long userId,
            @RequestParam String memberRole) {
        return service.issueBook(bookId, userId, memberRole);
    }

    @PostMapping("/books/issue/copy")
    public BookIssueRecord issueBookWithBarcode(
            @RequestParam Long bookId,
            @RequestParam Long userId,
            @RequestParam String barcode,
            @RequestParam String memberRole) {
        return service.issueBookWithBarcode(bookId, userId, barcode, memberRole);
    }

    @GetMapping("/members/{id}/eligibility")
    // @PreAuthorize("hasAuthority('BOOK_ISSUE_RECORD_VIEW')")
    public ResponseEntity<Boolean> checkEligibility(
            @PathVariable Long id,
            @RequestParam String memberRole) {
        return ResponseEntity.ok(service.validateEligibility(id, memberRole));
    }

    @PutMapping("/books/return/{id}")
    public BookIssueRecord returnBook(@PathVariable Long id) {
        return service.returnBook(id);
    }

    @GetMapping("/issues")
    // @PreAuthorize("hasAuthority('BOOK_ISSUE_RECORD_VIEW')")
    public List<BookIssueRecord> getIssues() {
        return service.getIssues();
    }

    // ================= RESERVATION =================

    @PostMapping("/reservations")
    // @PreAuthorize("hasAuthority('BOOK_VIEW')")
    public BookReservation createReservation(
            @RequestBody BookReservation reservation) {
        return service.createReservation(reservation);
    }

    @GetMapping("/reservations")
    // @PreAuthorize("hasAuthority('BOOK_RESERVATION_VIEW')")
    public List<BookReservation> getReservations() {
        return service.getReservations();
    }

    @DeleteMapping("/reservations/{id}")
    // @PreAuthorize("hasAuthority('BOOK_RESERVATION_VIEW')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        service.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reservations/{id}")
    // @PreAuthorize("hasAuthority('BOOK_RESERVATION_VIEW')")
    public BookReservation patchReservation(@PathVariable Long id, @RequestBody BookReservation updates) {
        // We'll need to implement a simple patch/update logic in service or just use
        // status update
        // Since frontend sends explicit status updates like { status: 'FULFILLED' }
        return service.updateReservationStatus(id, updates.getStatus());
    }

    // ================= SETTINGS =================

    @PostMapping("/settings")
    // @PreAuthorize("hasAuthority('LIBRARY_SETTING_CREATE')")
    public LibrarySettings saveSettings(
            @RequestBody LibrarySettings settings) {
        return service.saveSettings(settings);
    }

    @GetMapping("/settings")
    // @PreAuthorize("hasAuthority('LIBRARY_SETTING_VIEW')")
    public List<LibrarySettings> getSettings() {
        return service.getSettings();
    }

    // ================= FINES =================

    @GetMapping("/fines")
    // @PreAuthorize("hasAuthority('LIBRARY_FINE_VIEW')")
    public List<LibraryFine> getFines() {
        return service.getFines();
    }

    @GetMapping("/fines/user/{userId}")
    // @PreAuthorize("hasAuthority('LIBRARY_FINE_VIEW')")
    public List<LibraryFine> getFinesByUser(@PathVariable Long userId) {
        return service.getFinesByUserId(userId);
    }

    @PutMapping("/fines/{id}/pay")
    // @PreAuthorize("hasAuthority('LIBRARY_FINE_UPDATE')")
    public LibraryFine payFine(@PathVariable Long id) {
        return service.payFine(id);
    }

    @PutMapping("/fines/{id}/adjust")
    // @PreAuthorize("hasAuthority('LIBRARY_FINE_UPDATE')")
    public LibraryFine adjustFine(@PathVariable Long id, @RequestParam Double amount) {
        return service.adjustFine(id, amount);
    }

    @PostMapping("/books/lost/{issueId}")
    // @PreAuthorize("hasAuthority('BOOK_UPDATE')")
    public BookIssueRecord reportLostBook(@PathVariable Long issueId, @RequestParam Double penaltyAmount) {
        return service.reportLostBook(issueId, penaltyAmount);
    }

    // ================= BARCODES =================

    @PostMapping("/books/{id}/generate-barcodes")
    // @PreAuthorize("hasAuthority('BOOK_UPDATE')")
    public List<BookBarcode> generateBarcodes(@PathVariable Long id, @RequestParam int count) {
        return service.generateBarcodes(id, count);
    }

    // ================= DASHBOARD =================

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return service.getSummary();
    }

    @GetMapping("/trends")
    public List<Map<String, Object>> getTrends() {
        return service.getTrends();
    }

    @GetMapping("/recent-activity")
    public List<Map<String, Object>> getRecentActivity() {
        return service.getRecentActivity();
    }

    // ================= QR CODES =================

    // ================= QR CODES =================

    @GetMapping(value = "/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    // @PreAuthorize("hasAuthority('BOOK_VIEW')")
    public ResponseEntity<byte[]> getQRCode(@RequestParam String data) throws Exception {
        return ResponseEntity.ok(QRCodeUtil.generateQR(data));
    }
}
