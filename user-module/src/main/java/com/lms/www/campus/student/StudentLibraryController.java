package com.lms.www.campus.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.dto.StudentApiContract.*;
import com.lms.www.security.UserContext;
import com.lms.www.campus.Library.*;
import com.lms.www.campus.service.student.impl.StudentLibraryServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/library")
public class StudentLibraryController {

    private static final Logger log = LoggerFactory.getLogger(StudentLibraryController.class);

    @Autowired
    private StudentLibraryServiceImpl libraryService;

    @Autowired
    private UserContext userContext;

    @GetMapping("/books")
    @PreAuthorize("hasAuthority('BOOK_VIEW')")
    public ApiResponse<Page<LibraryBookDto>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching books for studentId: {} (page: {}, size: {})", studentId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Books> books = libraryService.getBooksForStudent(pageable);

        Page<LibraryBookDto> dtoPage = books.map(b -> LibraryBookDto.builder()
                .id(b.getId())
                .title(b.getTitle())
                .author(b.getAuthor())
                .publisher(b.getPublisher())
                .category(b.getCategory() != null ? b.getCategory().getCategoryName() : null)
                .language(b.getLanguage())
                .totalCopies(b.getTotalCopies())
                .availableCopies(b.getAvailableCopies())
                .type(b.getType() != null ? b.getType().name() : null)
                .build());

        return ApiResponse.success(dtoPage, "Books fetched successfully");
    }

    @GetMapping("/my-books")
    @PreAuthorize("hasAuthority('BOOK_ISSUE_RECORD_VIEW')")
    public ApiResponse<Page<LibraryIssueDto>> getMyBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching issued books for studentId: {}", studentId);

        Pageable pageable = PageRequest.of(page, size);
        Page<BookIssueRecord> issues = libraryService.getMyIssuedBooks(studentId, pageable);

        Page<LibraryIssueDto> dtoPage = issues.map(i -> LibraryIssueDto.builder()
                .issueId(i.getId())
                .bookId(i.getBook().getId())
                .bookTitle(i.getBook().getTitle())
                .issueDate(i.getIssueDate())
                .dueDate(i.getDueDate())
                .returnDate(i.getReturnDate())
                .status(i.getStatus().name())
                .build());

        return ApiResponse.success(dtoPage, "Issued books fetched successfully");
    }

    @GetMapping("/my-fines")
    @PreAuthorize("hasAuthority('LIBRARY_FINE_VIEW')")
    public ApiResponse<List<LibraryFineDto>> getMyFines() {
        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching fines for studentId: {}", studentId);

        List<LibraryFine> fines = libraryService.getMyFines(studentId);
        List<LibraryFineDto> dtos = fines.stream().map(f -> LibraryFineDto.builder()
                .fineId(f.getId())
                .bookTitle(f.getIssueRecord().getBook().getTitle())
                .amount(f.getFineAmount())
                .status(f.getPaidStatus().name())
                .generatedAt(f.getCreatedAt())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Fines fetched successfully");
    }

    @PostMapping("/reserve/{bookId}")
    @PreAuthorize("hasAuthority('BOOK_RESERVATION_CREATE')")
    public ApiResponse<LibraryReservationDto> reserveBook(@PathVariable Long bookId) {
        Long studentId = userContext.getCurrentUserId();
        log.info("Reserving book {} for studentId: {}", bookId, studentId);

        BookReservation res = libraryService.reserveBookForStudent(studentId, bookId);

        LibraryReservationDto dto = LibraryReservationDto.builder()
                .reservationId(res.getId())
                .bookId(res.getBook().getId())
                .bookTitle(res.getBook().getTitle())
                .reservedAt(res.getReservedAt())
                .status(res.getStatus().name())
                .build();

        return ApiResponse.success(dto, "Book reserved successfully");
    }

    @GetMapping("/reservations")
    @PreAuthorize("hasAuthority('BOOK_RESERVATION_VIEW')")
    public ApiResponse<List<LibraryReservationDto>> getMyReservations() {
        Long studentId = userContext.getCurrentUserId();

        List<BookReservation> reservations = libraryService.getMyReservations(studentId);
        List<LibraryReservationDto> dtos = reservations.stream().map(r -> LibraryReservationDto.builder()
                .reservationId(r.getId())
                .bookId(r.getBook().getId())
                .bookTitle(r.getBook().getTitle())
                .reservedAt(r.getReservedAt())
                .status(r.getStatus().name())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Reservations fetched successfully");
    }

    @DeleteMapping("/reservations/{id}")
    @PreAuthorize("hasAuthority('BOOK_RESERVATION_DELETE')")
    public ApiResponse<String> cancelReservation(@PathVariable Long id) {
        Long studentId = userContext.getCurrentUserId();
        log.info("Cancelling reservation {} for studentId: {}", id, studentId);

        libraryService.cancelMyReservation(studentId, id);
        return ApiResponse.success("Cancelled", "Reservation cancelled successfully");
    }
}
