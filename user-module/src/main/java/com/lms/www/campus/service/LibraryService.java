package com.lms.www.campus.service;

import java.util.List;
import java.util.Map;
import com.lms.www.campus.Library.*;

public interface LibraryService {
    // Category
    BookCategory createCategory(BookCategory category);
    List<BookCategory> getCategories();
    BookCategory updateCategory(Long id, BookCategory data);
    void deleteCategory(Long id);

    // Book
    Books createBook(Books book);
    List<Books> getBooks();
    List<Books> getBooksByCategory(Long categoryId);
    Books updateBook(Long id, Books data);
    void deleteBook(Long id);

    // Issue / Return
    BookIssueRecord issueBook(Long bookId, Long userId, String memberRole);
    BookIssueRecord issueBookWithBarcode(Long bookId, Long userId, String barcode, String memberRole);
    boolean validateEligibility(Long userId, String memberRole);
    BookIssueRecord returnBook(Long issueId);
    List<BookIssueRecord> getIssues();

    // Fines
    List<LibraryFine> getFines();
    List<LibraryFine> getFinesByUserId(Long userId);
    LibraryFine payFine(Long fineId);
    LibraryFine adjustFine(Long fineId, Double amount);

    // Barcodes
    List<BookBarcode> generateBarcodes(Long bookId, int count);

    // Reservation
    BookReservation createReservation(BookReservation reservation);
    List<BookReservation> getReservations();
    void deleteReservation(Long id);
    BookReservation updateReservationStatus(Long id, BookReservation.Status status);

    // Settings
    LibrarySettings saveSettings(LibrarySettings settings);
    List<LibrarySettings> getSettings();

    // Missing methods called by controller
    BookIssueRecord reportLostBook(Long issueId, Double penaltyAmount);
    Map<String, Object> getSummary();
    List<Map<String, Object>> getTrends();
    List<Map<String, Object>> getRecentActivity();
}
