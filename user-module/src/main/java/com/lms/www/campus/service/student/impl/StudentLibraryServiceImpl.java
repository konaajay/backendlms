package com.lms.www.campus.service.student.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.campus.Library.BookIssueRecord;
import com.lms.www.campus.Library.BookReservation;
import com.lms.www.campus.Library.Books;
import com.lms.www.campus.Library.LibraryFine;
import com.lms.www.campus.repository.Library.BookIssueRecordRepository;
import com.lms.www.campus.repository.Library.BookReservationRepository;
import com.lms.www.campus.repository.Library.BooksRepository;
import com.lms.www.campus.repository.Library.LibraryFineRepository;

@Service
@Transactional(readOnly = true)
public class StudentLibraryServiceImpl {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private BookIssueRecordRepository issueRepository;

    @Autowired
    private LibraryFineRepository libraryFineRepository;

    @Autowired
    private BookReservationRepository bookReservationRepository;

    public Page<Books> getBooksForStudent(Pageable pageable) {
        return booksRepository.findAll(pageable);
    }

    public Page<BookIssueRecord> getMyIssuedBooks(Long studentId, Pageable pageable) {
        return issueRepository.findByUserId(studentId, pageable);
    }

    public List<LibraryFine> getMyFines(Long studentId) {
        return libraryFineRepository.findByUserId(studentId);
    }

    @Transactional
    public BookReservation reserveBookForStudent(Long studentId, Long bookId) {
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() > 0) {
            throw new RuntimeException("Book is available, no need to reserve. Please collect from library.");
        }

        boolean alreadyReserved = bookReservationRepository.findByBook_IdAndUserIdAndIsDeletedFalse(bookId, studentId)
                .stream().anyMatch(r -> r.getStatus() == BookReservation.Status.RESERVED
                        || r.getStatus() == BookReservation.Status.AVAILABLE);

        if (alreadyReserved) {
            throw new RuntimeException("You have already reserved this book");
        }

        BookReservation res = new BookReservation();
        res.setBook(book);
        res.setUserId(studentId);
        res.setStatus(BookReservation.Status.RESERVED);
        res.setReservedAt(LocalDate.now());
        res.setIsDeleted(false);
        return bookReservationRepository.save(res);
    }

    public List<BookReservation> getMyReservations(Long studentId) {
        return bookReservationRepository.findByUserIdAndIsDeletedFalse(studentId);
    }

    @Transactional
    public void cancelMyReservation(Long studentId, Long reservationId) {
        BookReservation res = bookReservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!res.getUserId().equals(studentId)) {
            throw new AccessDeniedException("Cannot cancel another student's reservation");
        }

        res.setStatus(BookReservation.Status.CANCELLED);
        res.setIsDeleted(true);
        bookReservationRepository.save(res);
    }
}
