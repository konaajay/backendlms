package com.lms.www.campus.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.lms.www.dto.StudentApiContract.*;
import com.lms.www.security.UserContext;
import com.lms.www.campus.Library.*;
import com.lms.www.campus.service.student.impl.StudentLibraryServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/student/library")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentLibraryController {

    private static final Logger log = LoggerFactory.getLogger(StudentLibraryController.class);

    @Autowired
    private StudentLibraryServiceImpl libraryService;

    @Autowired
    private UserContext userContext;

    @GetMapping("/books")
    public ApiResponse<Page<LibraryBookDto>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching books (page: {}, size: {})", page, size);

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

    @GetMapping("/my/books")
    public ApiResponse<List<LibraryIssueDto>> getMyBooks() {
        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching active issued books for studentId: {}", studentId);

        List<BookIssueRecord> issues = libraryService.getMyIssuedBooks(studentId);

        List<LibraryIssueDto> dtos = issues.stream().map(i -> LibraryIssueDto.builder()
                .issueId(i.getId())
                .bookId(i.getBook() != null ? i.getBook().getId() : null)
                .bookTitle(i.getBook() != null ? i.getBook().getTitle() : "Unknown Book")
                .issueDate(i.getIssueDate())
                .dueDate(i.getDueDate())
                .returnDate(i.getReturnDate())
                .status(i.getStatus().name())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Active books fetched successfully");
    }

    @GetMapping("/my/history")
    public ApiResponse<List<LibraryIssueDto>> getMyHistory() {
        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching library history for studentId: {}", studentId);

        List<BookIssueRecord> history = libraryService.getMyLibraryHistory(studentId);

        List<LibraryIssueDto> dtos = history.stream().map(i -> LibraryIssueDto.builder()
                .issueId(i.getId())
                .bookId(i.getBook() != null ? i.getBook().getId() : null)
                .bookTitle(i.getBook() != null ? i.getBook().getTitle() : "Unknown Book")
                .issueDate(i.getIssueDate())
                .dueDate(i.getDueDate())
                .returnDate(i.getReturnDate())
                .status(i.getStatus().name())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "History fetched successfully");
    }

    @GetMapping("/my/fines")
    public ApiResponse<List<LibraryFineDto>> getMyFines() {
        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching fines for studentId: {}", studentId);

        List<LibraryFine> fines = libraryService.getMyFines(studentId);
        List<LibraryFineDto> dtos = fines.stream().map(f -> LibraryFineDto.builder()
                .fineId(f.getId())
                .bookTitle(f.getIssueRecord() != null && f.getIssueRecord().getBook() != null ? f.getIssueRecord().getBook().getTitle() : "N/A")
                .amount(f.getFineAmount())
                .status(f.getPaidStatus() != null ? f.getPaidStatus().name() : "UNPAID")
                .generatedAt(f.getCreatedAt())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Fines fetched successfully");
    }

    @PostMapping("/reserve/{bookId}")
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

    @GetMapping("/my/reservations")
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
    public ApiResponse<String> cancelReservation(@PathVariable Long id) {
        Long studentId = userContext.getCurrentUserId();
        log.info("Cancelling reservation {} for studentId: {}", id, studentId);

        libraryService.cancelMyReservation(studentId, id);
        return ApiResponse.success("Cancelled", "Reservation cancelled successfully");
    }
}
