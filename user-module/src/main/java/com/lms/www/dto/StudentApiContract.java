package com.lms.www.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentApiContract {

    public static class ApiResponse<T> {
        private String status;
        private int statusCode;
        private String message;
        private T data;
        private LocalDateTime timestamp = LocalDateTime.now();

        public ApiResponse() {}
        public ApiResponse(String status, int statusCode, String message, T data, LocalDateTime timestamp) {
            this.status = status;
            this.statusCode = statusCode;
            this.message = message;
            this.data = data;
            this.timestamp = timestamp;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public static <T> ApiResponseBuilder<T> builder() {
            return new ApiResponseBuilder<>();
        }

        public static <T> ApiResponse<T> success(T data, String message) {
            return new ApiResponse<>("SUCCESS", 200, message, data, LocalDateTime.now());
        }

        public static <T> ApiResponse<T> error(int statusCode, String message) {
            return new ApiResponse<>("ERROR", statusCode, message, null, LocalDateTime.now());
        }

        public static class ApiResponseBuilder<T> {
            private String status;
            private int statusCode;
            private String message;
            private T data;
            private LocalDateTime timestamp = LocalDateTime.now();

            public ApiResponseBuilder<T> status(String status) { this.status = status; return this; }
            public ApiResponseBuilder<T> statusCode(int statusCode) { this.statusCode = statusCode; return this; }
            public ApiResponseBuilder<T> message(String message) { this.message = message; return this; }
            public ApiResponseBuilder<T> data(T data) { this.data = data; return this; }
            public ApiResponseBuilder<T> timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
            public ApiResponse<T> build() {
                return new ApiResponse<>(status, statusCode, message, data, timestamp);
            }
        }
    }

    // ================= LIBRARY DTOS =================
    public static class LibraryBookDto {
        private Long id;
        private String title;
        private String author;
        private String publisher;
        private String category;
        private String language;
        private Integer totalCopies;
        private Integer availableCopies;
        private String type;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getPublisher() { return publisher; }
        public void setPublisher(String publisher) { this.publisher = publisher; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public Integer getTotalCopies() { return totalCopies; }
        public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
        public Integer getAvailableCopies() { return availableCopies; }
        public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public static LibraryBookDtoBuilder builder() { return new LibraryBookDtoBuilder(); }
        public static class LibraryBookDtoBuilder {
            private LibraryBookDto dto = new LibraryBookDto();
            public LibraryBookDtoBuilder id(Long id) { dto.id = id; return this; }
            public LibraryBookDtoBuilder title(String title) { dto.title = title; return this; }
            public LibraryBookDtoBuilder author(String author) { dto.author = author; return this; }
            public LibraryBookDtoBuilder publisher(String publisher) { dto.publisher = publisher; return this; }
            public LibraryBookDtoBuilder category(String category) { dto.category = category; return this; }
            public LibraryBookDtoBuilder language(String language) { dto.language = language; return this; }
            public LibraryBookDtoBuilder totalCopies(Integer totalCopies) { dto.totalCopies = totalCopies; return this; }
            public LibraryBookDtoBuilder availableCopies(Integer availableCopies) { dto.availableCopies = availableCopies; return this; }
            public LibraryBookDtoBuilder type(String type) { dto.type = type; return this; }
            public LibraryBookDto build() { return dto; }
        }
    }

    public static class LibraryIssueDto {
        private Long issueId;
        private Long bookId;
        private String bookTitle;
        private LocalDate issueDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private String status;

        public Long getIssueId() { return issueId; }
        public void setIssueId(Long issueId) { this.issueId = issueId; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public LocalDate getIssueDate() { return issueDate; }
        public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
        public LocalDate getDueDate() { return dueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
        public LocalDate getReturnDate() { return returnDate; }
        public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public static LibraryIssueDtoBuilder builder() { return new LibraryIssueDtoBuilder(); }
        public static class LibraryIssueDtoBuilder {
            private LibraryIssueDto dto = new LibraryIssueDto();
            public LibraryIssueDtoBuilder issueId(Long id) { dto.issueId = id; return this; }
            public LibraryIssueDtoBuilder bookId(Long id) { dto.bookId = id; return this; }
            public LibraryIssueDtoBuilder bookTitle(String title) { dto.bookTitle = title; return this; }
            public LibraryIssueDtoBuilder issueDate(LocalDate date) { dto.issueDate = date; return this; }
            public LibraryIssueDtoBuilder dueDate(LocalDate date) { dto.dueDate = date; return this; }
            public LibraryIssueDtoBuilder returnDate(LocalDate date) { dto.returnDate = date; return this; }
            public LibraryIssueDtoBuilder status(String status) { dto.status = status; return this; }
            public LibraryIssueDto build() { return dto; }
        }
    }

    public static class LibraryFineDto {
        private Long fineId;
        private String bookTitle;
        private Double amount;
        private String status;
        private LocalDateTime generatedAt;

        public Long getFineId() { return fineId; }
        public void setFineId(Long fineId) { this.fineId = fineId; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

        public static LibraryFineDtoBuilder builder() { return new LibraryFineDtoBuilder(); }
        public static class LibraryFineDtoBuilder {
            private LibraryFineDto dto = new LibraryFineDto();
            public LibraryFineDtoBuilder fineId(Long id) { dto.fineId = id; return this; }
            public LibraryFineDtoBuilder bookTitle(String title) { dto.bookTitle = title; return this; }
            public LibraryFineDtoBuilder amount(Double amount) { dto.amount = amount; return this; }
            public LibraryFineDtoBuilder status(String status) { dto.status = status; return this; }
            public LibraryFineDtoBuilder generatedAt(LocalDateTime date) { dto.generatedAt = date; return this; }
            public LibraryFineDto build() { return dto; }
        }
    }

    public static class LibraryReservationDto {
        private Long reservationId;
        private Long bookId;
        private String bookTitle;
        private LocalDate reservedAt;
        private String status;

        public Long getReservationId() { return reservationId; }
        public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public LocalDate getReservedAt() { return reservedAt; }
        public void setReservedAt(LocalDate reservedAt) { this.reservedAt = reservedAt; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public static LibraryReservationDtoBuilder builder() { return new LibraryReservationDtoBuilder(); }
        public static class LibraryReservationDtoBuilder {
            private LibraryReservationDto dto = new LibraryReservationDto();
            public LibraryReservationDtoBuilder reservationId(Long id) { dto.reservationId = id; return this; }
            public LibraryReservationDtoBuilder bookId(Long id) { dto.bookId = id; return this; }
            public LibraryReservationDtoBuilder bookTitle(String title) { dto.bookTitle = title; return this; }
            public LibraryReservationDtoBuilder reservedAt(LocalDate date) { dto.reservedAt = date; return this; }
            public LibraryReservationDtoBuilder status(String status) { dto.status = status; return this; }
            public LibraryReservationDto build() { return dto; }
        }
    }

    // ================= HOSTEL DTOS =================
    public static class HostelAllocationDto {
        private String hostelName;
        private String roomNumber;
        private LocalDate joinDate;
        private String status;

        public String getHostelName() { return hostelName; }
        public void setHostelName(String hostelName) { this.hostelName = hostelName; }
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
        public LocalDate getJoinDate() { return joinDate; }
        public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public static HostelAllocationDtoBuilder builder() { return new HostelAllocationDtoBuilder(); }
        public static class HostelAllocationDtoBuilder {
            private HostelAllocationDto dto = new HostelAllocationDto();
            public HostelAllocationDtoBuilder hostelName(String name) { dto.hostelName = name; return this; }
            public HostelAllocationDtoBuilder roomNumber(String room) { dto.roomNumber = room; return this; }
            public HostelAllocationDtoBuilder joinDate(LocalDate date) { dto.joinDate = date; return this; }
            public HostelAllocationDtoBuilder status(String status) { dto.status = status; return this; }
            public HostelAllocationDto build() { return dto; }
        }
    }

    public static class HostelAttendanceDto {
        private LocalDate attendanceDate;
        private String status;

        public LocalDate getAttendanceDate() { return attendanceDate; }
        public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public static HostelAttendanceDtoBuilder builder() { return new HostelAttendanceDtoBuilder(); }
        public static class HostelAttendanceDtoBuilder {
            private HostelAttendanceDto dto = new HostelAttendanceDto();
            public HostelAttendanceDtoBuilder attendanceDate(LocalDate date) { dto.attendanceDate = date; return this; }
            public HostelAttendanceDtoBuilder status(String status) { dto.status = status; return this; }
            public HostelAttendanceDto build() { return dto; }
        }
    }

    public static class HostelComplaintDto {
        private Long complaintId;
        private String issueCategory;
        private String description;
        private LocalDate reportedDate;
        private String status;
        private String adminRemarks;

        public Long getComplaintId() { return complaintId; }
        public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
        public String getIssueCategory() { return issueCategory; }
        public void setIssueCategory(String category) { this.issueCategory = category; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDate getReportedDate() { return reportedDate; }
        public void setReportedDate(LocalDate date) { this.reportedDate = date; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getAdminRemarks() { return adminRemarks; }
        public void setAdminRemarks(String remarks) { this.adminRemarks = remarks; }

        public static HostelComplaintDtoBuilder builder() { return new HostelComplaintDtoBuilder(); }
        public static class HostelComplaintDtoBuilder {
            private HostelComplaintDto dto = new HostelComplaintDto();
            public HostelComplaintDtoBuilder complaintId(Long id) { dto.complaintId = id; return this; }
            public HostelComplaintDtoBuilder issueCategory(String category) { dto.issueCategory = category; return this; }
            public HostelComplaintDtoBuilder description(String desc) { dto.description = desc; return this; }
            public HostelComplaintDtoBuilder reportedDate(LocalDate date) { dto.reportedDate = date; return this; }
            public HostelComplaintDtoBuilder status(String status) { dto.status = status; return this; }
            public HostelComplaintDtoBuilder adminRemarks(String remarks) { dto.adminRemarks = remarks; return this; }
            public HostelComplaintDto build() { return dto; }
        }
    }

    public static class HostelVisitDto {
        private Long visitId;
        private String visitorName;
        private LocalDate visitDate;
        private String status;

        public Long getVisitId() { return visitId; }
        public void setVisitId(Long visitId) { this.visitId = visitId; }
        public String getVisitorName() { return visitorName; }
        public void setVisitorName(String name) { this.visitorName = name; }
        public LocalDate getVisitDate() { return visitDate; }
        public void setVisitDate(LocalDate date) { this.visitDate = date; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public static HostelVisitDtoBuilder builder() { return new HostelVisitDtoBuilder(); }
        public static class HostelVisitDtoBuilder {
            private HostelVisitDto dto = new HostelVisitDto();
            public HostelVisitDtoBuilder visitId(Long id) { dto.visitId = id; return this; }
            public HostelVisitDtoBuilder visitorName(String name) { dto.visitorName = name; return this; }
            public HostelVisitDtoBuilder visitDate(LocalDate date) { dto.visitDate = date; return this; }
            public HostelVisitDtoBuilder status(String status) { dto.status = status; return this; }
            public HostelVisitDto build() { return dto; }
        }
    }

    public static class HostelFeeDto {
        private Double totalFee;
        private Double amountPaid;
        private Double dueAmount;
        private String status;

        public Double getTotalFee() { return totalFee; }
        public void setTotalFee(Double totalFee) { this.totalFee = totalFee; }
        public Double getAmountPaid() { return amountPaid; }
        public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }
        public Double getDueAmount() { return dueAmount; }
        public void setDueAmount(Double dueAmount) { this.dueAmount = dueAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public static HostelFeeDtoBuilder builder() { return new HostelFeeDtoBuilder(); }
        public static class HostelFeeDtoBuilder {
            private HostelFeeDto dto = new HostelFeeDto();
            public HostelFeeDtoBuilder totalFee(Double fee) { dto.totalFee = fee; return this; }
            public HostelFeeDtoBuilder amountPaid(Double paid) { dto.amountPaid = paid; return this; }
            public HostelFeeDtoBuilder dueAmount(Double due) { dto.dueAmount = due; return this; }
            public HostelFeeDtoBuilder status(String status) { dto.status = status; return this; }
            public HostelFeeDto build() { return dto; }
        }
    }

    // ================= TRANSPORT DTOS =================
    public static class TransportAllocationDto {
        private String routeName;
        private String vehicleNumber;
        private String pickupPoint;
        private String dropPoint;
        private String shift;

        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String number) { this.vehicleNumber = number; }
        public String getPickupPoint() { return pickupPoint; }
        public void setPickupPoint(String point) { this.pickupPoint = point; }
        public String getDropPoint() { return dropPoint; }
        public void setDropPoint(String point) { this.dropPoint = point; }
        public String getShift() { return shift; }
        public void setShift(String shift) { this.shift = shift; }

        public static TransportAllocationDtoBuilder builder() { return new TransportAllocationDtoBuilder(); }
        public static class TransportAllocationDtoBuilder {
            private TransportAllocationDto dto = new TransportAllocationDto();
            public TransportAllocationDtoBuilder routeName(String name) { dto.routeName = name; return this; }
            public TransportAllocationDtoBuilder vehicleNumber(String number) { dto.vehicleNumber = number; return this; }
            public TransportAllocationDtoBuilder pickupPoint(String point) { dto.pickupPoint = point; return this; }
            public TransportAllocationDtoBuilder dropPoint(String point) { dto.dropPoint = point; return this; }
            public TransportAllocationDtoBuilder shift(String shift) { dto.shift = shift; return this; }
            public TransportAllocationDto build() { return dto; }
        }
    }

    public static class TransportAttendanceDto {
        private LocalDate attendanceDate;
        private String pickupStatus;
        private String dropStatus;
        private String routeName;

        public LocalDate getAttendanceDate() { return attendanceDate; }
        public void setAttendanceDate(LocalDate date) { this.attendanceDate = date; }
        public String getPickupStatus() { return pickupStatus; }
        public void setPickupStatus(String status) { this.pickupStatus = status; }
        public String getDropStatus() { return dropStatus; }
        public void setDropStatus(String status) { this.dropStatus = status; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String name) { this.routeName = name; }

        public static TransportAttendanceDtoBuilder builder() { return new TransportAttendanceDtoBuilder(); }
        public static class TransportAttendanceDtoBuilder {
            private TransportAttendanceDto dto = new TransportAttendanceDto();
            public TransportAttendanceDtoBuilder attendanceDate(LocalDate date) { dto.attendanceDate = date; return this; }
            public TransportAttendanceDtoBuilder pickupStatus(String status) { dto.pickupStatus = status; return this; }
            public TransportAttendanceDtoBuilder dropStatus(String status) { dto.dropStatus = status; return this; }
            public TransportAttendanceDtoBuilder routeName(String name) { dto.routeName = name; return this; }
            public TransportAttendanceDto build() { return dto; }
        }
    }

    public static class TransportFeeDto {
        private Double totalFee;
        private Double amountPaid;
        private Double dueAmount;
        private String status;

        public Double getTotalFee() { return totalFee; }
        public void setTotalFee(Double totalFee) { this.totalFee = totalFee; }
        public Double getAmountPaid() { return amountPaid; }
        public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }
        public Double getDueAmount() { return dueAmount; }
        public void setDueAmount(Double dueAmount) { this.dueAmount = dueAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public static TransportFeeDtoBuilder builder() { return new TransportFeeDtoBuilder(); }
        public static class TransportFeeDtoBuilder {
            private TransportFeeDto dto = new TransportFeeDto();
            public TransportFeeDtoBuilder totalFee(Double fee) { dto.totalFee = fee; return this; }
            public TransportFeeDtoBuilder amountPaid(Double paid) { dto.amountPaid = paid; return this; }
            public TransportFeeDtoBuilder dueAmount(Double due) { dto.dueAmount = due; return this; }
            public TransportFeeDtoBuilder status(String status) { dto.status = status; return this; }
            public TransportFeeDto build() { return dto; }
        }
    }

    // ================= DOCUMENT DTOS =================
    public static class DocumentDto {
        private Long documentId;
        private String documentName;
        private String documentType;
        private String status;
        private LocalDateTime uploadedAt;

        public Long getDocumentId() { return documentId; }
        public void setDocumentId(Long documentId) { this.documentId = documentId; }
        public String getDocumentName() { return documentName; }
        public void setDocumentName(String name) { this.documentName = name; }
        public String getDocumentType() { return documentType; }
        public void setDocumentType(String type) { this.documentType = type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(LocalDateTime date) { this.uploadedAt = date; }

        public static DocumentDtoBuilder builder() { return new DocumentDtoBuilder(); }
        public static class DocumentDtoBuilder {
            private DocumentDto dto = new DocumentDto();
            public DocumentDtoBuilder documentId(Long id) { dto.documentId = id; return this; }
            public DocumentDtoBuilder documentName(String name) { dto.documentName = name; return this; }
            public DocumentDtoBuilder documentType(String type) { dto.documentType = type; return this; }
            public DocumentDtoBuilder status(String status) { dto.status = status; return this; }
            public DocumentDtoBuilder uploadedAt(LocalDateTime date) { dto.uploadedAt = date; return this; }
            public DocumentDto build() { return dto; }
        }
    }
}
