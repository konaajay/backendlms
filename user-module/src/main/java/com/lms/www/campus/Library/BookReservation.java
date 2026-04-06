package com.lms.www.campus.Library;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Entity
@Table(name = "book_reservations")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookReservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reservation_id")
	private Long id;

	// Book being reserved
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "book_id", nullable = false)
	private Books book;

	// User who reserved (kept as ID intentionally)
	@Column(name = "user_id", nullable = false)
	@JsonProperty("userId")
	private Long userId;

	// ===== USER RESERVATION WINDOW =====

	@Column(name = "reserved_at", nullable = false)
	@JsonProperty("reservedAt")
	private LocalDate reservedAt;

	@Column(name = "reservation_date", nullable = true)
	@JsonProperty("reservationDate")
	private LocalDate reservationDate;

	@Column(name = "reserve_until", nullable = true)
	@JsonProperty("reserveUntil")
	private LocalDate reserveUntil;

	// ===== ADMIN HOLD WINDOW =====

	@Column(name = "admin_hold_from", nullable = true)
	private LocalDate adminHoldFrom;

	@Column(name = "admin_hold_until", nullable = true)
	private LocalDate adminHoldUntil;

	// ===== STATUS =====

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	// ===== SOFT DELETE =====

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	// ===== AUDIT =====

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// ===== LIFECYCLE =====

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;

		if (reservedAt == null) {
			reservedAt = LocalDate.now();
		}

		if (status == null) {
			status = Status.RESERVED;
		}

		if (isDeleted == null) {
			isDeleted = false;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Books getBook() { return book; }
	public void setBook(Books book) { this.book = book; }

	@JsonProperty("bookId")
	public void setBookId(Long bookId) {
		if (this.book == null) {
			this.book = new Books();
		}
		this.book.setId(bookId);
	}

	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public LocalDate getReservedAt() { return reservedAt; }
	public void setReservedAt(LocalDate reservedAt) { this.reservedAt = reservedAt; }
	public LocalDate getReservationDate() { return reservationDate; }
	public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }
	public LocalDate getReserveUntil() { return reserveUntil; }
	public void setReserveUntil(LocalDate reserveUntil) { this.reserveUntil = reserveUntil; }
	public LocalDate getAdminHoldFrom() { return adminHoldFrom; }
	public void setAdminHoldFrom(LocalDate adminHoldFrom) { this.adminHoldFrom = adminHoldFrom; }
	public LocalDate getAdminHoldUntil() { return adminHoldUntil; }
	public void setAdminHoldUntil(LocalDate adminHoldUntil) { this.adminHoldUntil = adminHoldUntil; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
	public Boolean getIsDeleted() { return isDeleted; }
	public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

	// ===== STATUS FLOW =====
	public enum Status {
		RESERVED, // user hold window active
		AVAILABLE, // admin hold window active
		COLLECTED, // book collected
		CANCELLED, // cancelled manually
		NO_RESPONSE // user never came
	}
}