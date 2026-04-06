package com.lms.www.campus.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.campus.Library.*;
import com.lms.www.campus.repository.Library.*;
import com.lms.www.campus.service.LibraryService;
import com.lms.www.repository.UserRepository;
import java.util.Arrays;

@Service
@Transactional
public class LibraryServiceImpl implements LibraryService {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private BookIssueRecordRepository issueRepository;

    @Autowired
    private BookReservationRepository bookReservationRepository;

    @Autowired
    private LibraryFineRepository libraryFineRepository;

    @Autowired
    private LibrarySettingsRepository librarySettingsRepository;

    @Autowired
    private BookBarcodeRepository bookBarcodeRepository;

    @Autowired
    private FineCalculationService fineCalculationService;

    @Autowired
    private UserRepository userRepository;

    // ================= CATEGORY =================

    @Override
    public BookCategory createCategory(BookCategory category) {
        if (category.getCategoryName() == null || category.getCategoryName().isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
        return bookCategoryRepository.save(category);
    }

    @Override
    public List<BookCategory> getCategories() {
        return bookCategoryRepository.findByIsDeletedFalse();
    }

    @Override
    public BookCategory updateCategory(Long id, BookCategory data) {
        BookCategory category = bookCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setCategoryName(data.getCategoryName());
        category.setDescription(data.getDescription());
        category.setStatus(data.getStatus());

        return bookCategoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        BookCategory category = bookCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setIsDeleted(true);
        bookCategoryRepository.save(category);
    }

    // ================= BOOK =================

    @Override
    public Books createBook(Books book) {
        if (book.getCategory() == null || book.getCategory().getId() == null) {
            throw new IllegalArgumentException("Category is required");
        }

        Long categoryId = book.getCategory().getId();
        if (categoryId == null) throw new IllegalArgumentException("Category ID cannot be null");
        BookCategory category = bookCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Invalid category"));

        book.setCategory(category);
        sanitizeBook(book);

        if (book.getTotalCopies() == null) {
            book.setTotalCopies(1);
        }
        book.setAvailableCopies(book.getTotalCopies());

        Books savedBook = booksRepository.save(book);

        if (savedBook.getTotalCopies() != null && savedBook.getTotalCopies() > 0) {
            generateBarcodes(savedBook.getId(), savedBook.getTotalCopies());
        }

        return savedBook;
    }

    @Override
    public List<Books> getBooks() {
        List<Books> books = booksRepository.findByIsDeletedFalse();
        // Optional: Perform a quick sync check for books showing 0 availability 
        // to auto-correct any ghost stock issues on load.
        books.forEach(this::syncInventory);
        return books;
    }

    @Override
    public List<Books> getBooksByCategory(Long categoryId) {
        return booksRepository.findByCategory_IdAndIsDeletedFalse(categoryId);
    }

    @Override
    public Books updateBook(Long id, Books data) {
        Books book = booksRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        sanitizeBook(data);

        book.setTitle(data.getTitle());
        book.setAuthor(data.getAuthor());
        book.setPublisher(data.getPublisher());
        book.setEdition(data.getEdition());
        book.setYear(data.getYear());
        book.setLanguage(data.getLanguage());
        book.setAccessUrl(data.getAccessUrl());
        book.setFormat(data.getFormat());
        book.setDigitalType(data.getDigitalType());
        book.setLicenseExpiry(data.getLicenseExpiry());
        book.setUsageLimit(data.getUsageLimit());
        book.setIsbn(data.getIsbn());
        book.setShelfLocation(data.getShelfLocation());
        book.setTotalCopies(data.getTotalCopies());
        book.setStatus(data.getStatus());

        // Auto-fix inventory drift if any negative values detected
        syncInventory(book);

        if (data.getType() != null) {
            book.setType(data.getType());
        }

        if (data.getCategory() != null && data.getCategory().getId() != null) {
            BookCategory category = bookCategoryRepository.findById(data.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid category"));
            book.setCategory(category);
        }

        return booksRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        Books book = booksRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setIsDeleted(true);
        booksRepository.save(book);
    }

    // ================= ISSUE / RETURN =================

    @Override
    public BookIssueRecord issueBook(Long bookId, Long userId, String memberRole) {
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        List<BookReservation> userReservations = bookReservationRepository.findByBook_IdAndUserIdAndIsDeletedFalse(
                bookId,
                userId);

        BookReservation activeRes = userReservations.stream()
                .filter(r -> r.getStatus() == BookReservation.Status.AVAILABLE
                        || r.getStatus() == BookReservation.Status.RESERVED)
                .findFirst().orElse(null);

        boolean isHeldReservation = (activeRes != null && activeRes.getStatus() == BookReservation.Status.AVAILABLE);

        if (!isHeldReservation && book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book not available");
        }

        LibrarySettings settings = librarySettingsRepository.findByMemberRoleAndIsDeletedFalse(memberRole)
                .orElse(null);

        int issueDuration = 14;
        int maxBooks = 3;

        if (settings != null) {
            if (settings.getIssueDurationDays() != null) {
                issueDuration = settings.getIssueDurationDays();
            }
            if (settings.getMaxBooks() != null) {
                maxBooks = settings.getMaxBooks();
            }
        }
        long issuedCount = issueRepository.countByUserIdAndStatus(userId, BookIssueRecord.Status.ISSUED);

        if (issuedCount >= maxBooks) {
            throw new RuntimeException("Book limit exceeded (Max: " + maxBooks + ")");
        }

        if (activeRes != null) {
            activeRes.setStatus(BookReservation.Status.COLLECTED);
            bookReservationRepository.save(activeRes);
        }

        BookIssueRecord issueRecord = new BookIssueRecord();
        issueRecord.setBook(book);
        issueRecord.setUserId(userId);
        issueRecord.setUserCategory(memberRole);
        issueRecord.setIssueDate(LocalDate.now());
        issueRecord.setDueDate(LocalDate.now().plusDays(issueDuration));
        issueRecord.setStatus(BookIssueRecord.Status.ISSUED);
        BookIssueRecord saved = issueRepository.save(issueRecord);

        // Sync AFTER persisting the new record
        syncInventory(book);
        booksRepository.saveAndFlush(book);

        return saved;
    }

    @Override
    public BookIssueRecord issueBookWithBarcode(Long bookId, Long userId, String barcode, String memberRole) {
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        List<BookReservation> userReservations = bookReservationRepository.findByBook_IdAndUserIdAndIsDeletedFalse(
                bookId,
                userId);
        BookReservation activeRes = userReservations.stream()
                .filter(r -> r.getStatus() == BookReservation.Status.AVAILABLE
                        || r.getStatus() == BookReservation.Status.RESERVED)
                .findFirst().orElse(null);

        boolean isHeldReservation = (activeRes != null && activeRes.getStatus() == BookReservation.Status.AVAILABLE);

        // If it's a barcode-based issue, the physical existence of the book (valid barcode scanned) 
        // implies availability. We only strictly check availableCopies for barcode-less issues.
        if (barcode == null && !isHeldReservation && book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book not available");
        }

        LibrarySettings settings = librarySettingsRepository.findByMemberRoleAndIsDeletedFalse(memberRole)
                .orElse(null);
        int issueDuration = 14;
        int maxBooks = 3;

        if (settings != null) {
            if (settings.getIssueDurationDays() != null)
                issueDuration = settings.getIssueDurationDays();
            if (settings.getMaxBooks() != null)
                maxBooks = settings.getMaxBooks();
        }

        long issuedCount = issueRepository.countByUserIdAndStatus(userId, BookIssueRecord.Status.ISSUED);
        if (issuedCount >= maxBooks) {
            throw new RuntimeException("Book limit exceeded (Max: " + maxBooks + ")");
        }

        if (barcode != null) {
            BookBarcode bc = book.getBarcodes().stream()
                    .filter(b -> b.getBarcodeValue().equals(barcode))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Barcode not found"));

            if (bc.getIsIssued()) {
                throw new RuntimeException("This copy is already issued");
            }

            bc.setIsIssued(true);
        }

        if (activeRes != null) {
            activeRes.setStatus(BookReservation.Status.COLLECTED);
            bookReservationRepository.save(activeRes);
        }

        BookIssueRecord issueRecord = new BookIssueRecord();
        issueRecord.setBook(book);
        issueRecord.setUserId(userId);
        issueRecord.setUserCategory(memberRole);
        issueRecord.setBarcodeValue(barcode);
        issueRecord.setIssueDate(LocalDate.now());
        issueRecord.setDueDate(LocalDate.now().plusDays(issueDuration));
        issueRecord.setStatus(BookIssueRecord.Status.ISSUED);
        BookIssueRecord saved = issueRepository.save(issueRecord);

        // Sync AFTER persisting the new record
        syncInventory(book);
        booksRepository.saveAndFlush(book);

        return saved;
    }

    @Override
    public boolean validateEligibility(Long userId, String memberRole) {
        LibrarySettings settings = librarySettingsRepository.findByMemberRoleAndIsDeletedFalse(memberRole)
                .orElse(null);

        if (settings == null) {
            long issuedCount = issueRepository.countByUserIdAndStatus(userId, BookIssueRecord.Status.ISSUED);
            return issuedCount < 3;
        }

        long issuedCount = issueRepository.countByUserIdAndStatus(userId, BookIssueRecord.Status.ISSUED);
        int max = (settings.getMaxBooks() != null) ? settings.getMaxBooks() : 3;
        return issuedCount < max;
    }

    @Override
    public BookIssueRecord returnBook(Long issueId) {
        BookIssueRecord issueRecord = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue record not found"));

        issueRecord.setReturnDate(LocalDate.now());
        issueRecord.setStatus(BookIssueRecord.Status.RETURNED);

        // Persist return status FIRST so syncInventory reads correct issuedCount from DB
        issueRepository.saveAndFlush(issueRecord);

        if (issueRecord.getReturnDate().isAfter(issueRecord.getDueDate())) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(issueRecord.getDueDate(),
                    issueRecord.getReturnDate());
            String memberRole = issueRecord.getUserCategory();
            LibrarySettings settings = librarySettingsRepository.findByMemberRoleAndIsDeletedFalse(memberRole)
                    .orElse(librarySettingsRepository.findFirstByIsDeletedFalse().orElse(null));

            if (settings != null && settings.getSettingId() != null) {
                double fineAmount = fineCalculationService.calculateFine(settings.getSettingId(), (int) days);
                if (fineAmount > 0) {
                    LibraryFine fine = new LibraryFine();
                    fine.setIssueRecord(issueRecord);
                    fine.setUserId(issueRecord.getUserId());
                    fine.setFineAmount(fineAmount);
                    fine.setPaidStatus(LibraryFine.Status.UNPAID);
                    libraryFineRepository.save(fine);
                }
            }
        }

        Books book = issueRecord.getBook();
        List<BookReservation> reservations = bookReservationRepository.findByBook_IdAndIsDeletedFalse(book.getId());

        BookReservation pendingReservation = reservations.stream()
                .filter(r -> r.getStatus() == BookReservation.Status.RESERVED)
                .min((r1, r2) -> r1.getReservedAt().compareTo(r2.getReservedAt()))
                .orElse(null);

        if (pendingReservation != null) {
            // Mark reservation as ready for pickup
            pendingReservation.setStatus(BookReservation.Status.AVAILABLE);
            pendingReservation.setAdminHoldFrom(LocalDate.now());
            int holdDays = 5;
            LibrarySettings settings = librarySettingsRepository.findByMemberRoleAndIsDeletedFalse("Student")
                    .orElse(null);
            if (settings != null && settings.getReservationDurationDays() != null) {
                holdDays = settings.getReservationDurationDays();
            }
            pendingReservation.setAdminHoldUntil(LocalDate.now().plusDays(holdDays));
            bookReservationRepository.save(pendingReservation);

            // Recalculate: issuedCount=0, heldCount=1 → availableCopies stays same (held for reservation)
            syncInventory(book);
            // Force RESERVED status regardless of available copy count
            book.setStatus(Books.Status.RESERVED);
        } else {
            // Recalculate: issuedCount=0, heldCount=0 → availableCopies correctly increases
            syncInventory(book);
            // syncInventory already sets AVAILABLE/UNAVAILABLE based on count
        }

        booksRepository.saveAndFlush(book);

        if (issueRecord.getBarcodeValue() != null) {
            bookBarcodeRepository.findByBarcodeValue(issueRecord.getBarcodeValue())
                    .ifPresent(bc -> {
                        bc.setIsIssued(false);
                        bookBarcodeRepository.save(bc);
                    });
        }

        return issueRecord; // Already saved via saveAndFlush above
    }

    @Override
    public List<BookIssueRecord> getIssues() {
        return issueRepository.findAll();
    }

    // ================= FINES =================

    @Override
    public List<LibraryFine> getFines() {
        return libraryFineRepository.findAll();
    }

    @Override
    public List<LibraryFine> getFinesByUserId(Long userId) {
        return libraryFineRepository.findByUserId(userId);
    }

    @Override
    public LibraryFine payFine(Long fineId) {
        LibraryFine fine = libraryFineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));
        fine.setPaidStatus(LibraryFine.Status.PAID);
        return libraryFineRepository.save(fine);
    }

    @Override
    public LibraryFine adjustFine(Long fineId, Double amount) {
        LibraryFine fine = libraryFineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));
        fine.setFineAmount(amount);
        return libraryFineRepository.save(fine);
    }

    // ================= BARCODES =================

    @Override
    public List<BookBarcode> generateBarcodes(Long bookId, int count) {
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        List<BookBarcode> barcodes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BookBarcode bc = new BookBarcode();
            bc.setBook(book);
            String prefix = (book.getIsbn() != null) ? book.getIsbn() : "BK-" + book.getId();
            bc.setBarcodeValue(prefix + "-" + System.currentTimeMillis() + "-" + i);
            bc.setIsIssued(false);
            barcodes.add(bookBarcodeRepository.save(bc));
        }
        return barcodes;
    }

    // ================= RESERVATION =================

    @Override
    public BookReservation createReservation(BookReservation reservation) {
        if (reservation.getBook() == null || reservation.getBook().getId() == null) {
            throw new IllegalArgumentException("Book is required");
        }
        if (reservation.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        Books book = booksRepository.findById(reservation.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        reservation.setBook(book);

        if (book.getAvailableCopies() > 0) {
            throw new RuntimeException(
                    "Book is currently available. No reservation needed. Please visit the library to collect it.");
        }

        reservation.setReservedAt(LocalDate.now());
        if (reservation.getReservationDate() == null)
            reservation.setReservationDate(null);
        if (reservation.getReserveUntil() == null)
            reservation.setReserveUntil(null);
        reservation.setAdminHoldFrom(null);
        reservation.setAdminHoldUntil(null);
        reservation.setStatus(BookReservation.Status.RESERVED);
        reservation.setIsDeleted(false);

        return bookReservationRepository.save(reservation);
    }

    @Override
    public List<BookReservation> getReservations() {
        return bookReservationRepository.findByIsDeletedFalse();
    }

    @Override
    public void deleteReservation(Long id) {
        BookReservation res = bookReservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        res.setIsDeleted(true);
        bookReservationRepository.save(res);
        
        // Recalculate inventory - syncInventory handles availableCopies and Status updates
        Books book = res.getBook();
        if (book != null) {
            syncInventory(book);
            booksRepository.saveAndFlush(book);
        }
    }

    @Override
    public BookReservation updateReservationStatus(Long id, BookReservation.Status status) {
        BookReservation res = bookReservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (status == BookReservation.Status.COLLECTED) {
            if (res.getStatus() == BookReservation.Status.AVAILABLE &&
                    res.getAdminHoldUntil() != null &&
                    res.getAdminHoldUntil().isBefore(LocalDate.now())) {

                res.setStatus(BookReservation.Status.NO_RESPONSE);
                bookReservationRepository.save(res);
                
                // Pickup window expired, release the hold
                syncInventory(res.getBook());
                booksRepository.saveAndFlush(res.getBook());
                
                throw new RuntimeException("Pickup window expired");
            }
        }

        res.setStatus(status);
        bookReservationRepository.saveAndFlush(res);
        
        // After any status change, sync the book's inventory counts
        Books b = res.getBook();
        if (b != null) {
            syncInventory(b);
            // For multi-copy books, if at least one copy is still held for someone else, stay RESERVED
            if (b.getStatus() != Books.Status.LOST) {
                long heldCount = bookReservationRepository.countByBook_IdAndStatusAndIsDeletedFalse(b.getId(),
                        BookReservation.Status.AVAILABLE);
                if (heldCount > 0) {
                    b.setStatus(Books.Status.RESERVED);
                }
            }
            booksRepository.saveAndFlush(b);
        }

        return res;
    }

    // ================= SETTINGS =================

    @Override
    public LibrarySettings saveSettings(LibrarySettings settings) {
        if (settings.getMemberRole() == null) {
            throw new IllegalArgumentException("Member Role is required");
        }

        if (settings.getIsDeleted() == null) {
            settings.setIsDeleted(false);
        }

        LibrarySettings existingSettings = librarySettingsRepository
                .findByMemberRoleAndIsDeletedFalse(settings.getMemberRole()).orElse(null);

        if (existingSettings != null) {
            existingSettings.setMaxBooks(settings.getMaxBooks());
            existingSettings.setIssueDurationDays(settings.getIssueDurationDays());
            existingSettings.setReservationDurationDays(settings.getReservationDurationDays());
            existingSettings.setIsDeleted(settings.getIsDeleted());

            if (settings.getFineSlabs() != null) {
                existingSettings.getFineSlabs().clear();
                for (FineSlab slab : settings.getFineSlabs()) {
                    slab.setLibrarySettings(existingSettings);
                    slab.setMemberRole(existingSettings.getMemberRole());
                    existingSettings.getFineSlabs().add(slab);
                }
            }
            return librarySettingsRepository.save(existingSettings);
        } else {
            if (settings.getFineSlabs() != null) {
                for (FineSlab slab : settings.getFineSlabs()) {
                    slab.setLibrarySettings(settings);
                    slab.setMemberRole(settings.getMemberRole());
                }
            }
            return librarySettingsRepository.save(settings);
        }
    }

    @Override
    public List<LibrarySettings> getSettings() {
        return librarySettingsRepository.findByIsDeletedFalse();
    }

    @Override
    public BookIssueRecord reportLostBook(Long issueId, Double penaltyAmount) {
        BookIssueRecord issueRecord = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue record not found"));

        Books book = issueRecord.getBook();
        book.setStatus(Books.Status.LOST);
        booksRepository.save(book);

        if (penaltyAmount > 0) {
            LibraryFine fine = new LibraryFine();
            fine.setIssueRecord(issueRecord);
            fine.setUserId(issueRecord.getUserId());
            fine.setFineAmount(penaltyAmount);
            fine.setPaidStatus(LibraryFine.Status.UNPAID);
            libraryFineRepository.save(fine);
        }

        issueRecord.setStatus(BookIssueRecord.Status.LOST);
        return issueRepository.save(issueRecord);
    }

    @Override
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Total unique titles
        long totalTitles = booksRepository.countByIsDeletedFalse();

        // Total physical copies in catalog
        Integer totalResources = booksRepository.findByIsDeletedFalse().stream()
                .mapToInt(b -> b.getTotalCopies() != null ? b.getTotalCopies() : 0)
                .sum();

        long activeIssues = issueRepository.countByStatusIn(Arrays.asList(
                BookIssueRecord.Status.ISSUED,
                BookIssueRecord.Status.OVERDUE));

        long overdue = issueRepository.countByStatus(BookIssueRecord.Status.OVERDUE);

        long activeMembers = issueRepository.countActiveMembers();
        long totalMembers = userRepository.count();

        summary.put("totalResources", totalResources);
        summary.put("totalTitles", totalTitles);
        summary.put("activeIssues", activeIssues);
        summary.put("overdue", overdue);
        summary.put("activeMembers", activeMembers);
        summary.put("totalMembers", totalMembers);
        summary.put("digitalAccess", 0);

        return summary;
    }

    @Override
    public List<Map<String, Object>> getTrends() {
        List<Map<String, Object>> trends = new ArrayList<>();
        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

        // Simple mock data for trends that scales with total issues
        long baseCount = issueRepository.count();
        for (String day : days) {
            Map<String, Object> point = new HashMap<>();
            point.put("name", day);
            point.put("issues", (int) (baseCount * (0.5 + Math.random() * 0.5)));
            trends.add(point);
        }
        return trends;
    }

    @Override
    public List<Map<String, Object>> getRecentActivity() {
        List<BookIssueRecord> records = issueRepository.findTop10ByOrderByCreatedAtDesc();
        List<Map<String, Object>> activity = new ArrayList<>();

        for (BookIssueRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("user", "Member #" + record.getUserId());
            item.put("date", record.getCreatedAt());
            item.put("type", record.getStatus().toString());
            item.put("resource", record.getBook() != null ? record.getBook().getTitle() : "N/A");
            activity.add(item);
        }
        return activity;
    }

    private void sanitizeBook(Books book) {
        if (book.getIsbn() != null && book.getIsbn().isBlank())
            book.setIsbn(null);
        if (book.getShelfLocation() != null && book.getShelfLocation().isBlank())
            book.setShelfLocation(null);
        if (book.getLicenseExpiry() != null && book.getLicenseExpiry().isBlank())
            book.setLicenseExpiry(null);
        if (book.getAccessUrl() != null && book.getAccessUrl().isBlank())
            book.setAccessUrl(null);
        if (book.getDigitalType() != null && book.getDigitalType().isBlank())
            book.setDigitalType(null);
        if (book.getYear() != null && book.getYear().isBlank())
            book.setYear(null);
        if (book.getPublisher() != null && book.getPublisher().isBlank())
            book.setPublisher(null);
        if (book.getEdition() != null && book.getEdition().isBlank())
            book.setEdition(null);
        if (book.getLanguage() != null && book.getLanguage().isBlank())
            book.setLanguage(null);
    }

    private void syncInventory(Books book) {
        if (book == null || book.getId() == null)
            return;

        Long bId = book.getId();
        if (bId == null) return;
        
        long issuedCount = issueRepository.countByBook_IdAndStatusIn(bId, Arrays.asList(
                BookIssueRecord.Status.ISSUED,
                BookIssueRecord.Status.OVERDUE));

        long heldCount = bookReservationRepository.countByBook_IdAndStatusAndIsDeletedFalse(bId,
                BookReservation.Status.AVAILABLE);

        int calculatedAvailable = Math.max(0, book.getTotalCopies() - (int) issuedCount - (int) heldCount);

        // Update if drift detected or negative
        if (book.getAvailableCopies() != calculatedAvailable || book.getAvailableCopies() < 0) {
            book.setAvailableCopies(calculatedAvailable);
            if (calculatedAvailable == 0 && book.getStatus() == Books.Status.AVAILABLE) {
                book.setStatus(Books.Status.UNAVAILABLE);
            } else if (calculatedAvailable > 0 && book.getStatus() == Books.Status.UNAVAILABLE) {
                book.setStatus(Books.Status.AVAILABLE);
            }
        }
    }
}
