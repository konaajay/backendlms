package com.lms.www.campus.Library;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Books {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String author;

	@Column(name = "publisher")
	private String publisher;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", nullable = false)
	private BookCategory category;

	@Column(name = "edition")
	private String edition;

	@Column(name = "year", length = 4)
	private String year;

	@Column(name = "language")
	private String language;

	@Column(name = "access_url")
	private String accessUrl;

	@Column(name = "format")
	private String format;

	@Column(name = "digital_type")
	private String digitalType;

	@Column(name = "license_expiry")
	private String licenseExpiry;

	@Column(name = "usage_limit")
	private Integer usageLimit;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookType type = BookType.PHYSICAL;

	@Column(nullable = true, unique = true)
	private String isbn;

	@Column(name = "shelf_location", nullable = true)
	private String shelfLocation;

	@Column(name = "total_copies", nullable = false)
	private Integer totalCopies;

	@Column(name = "available_copies", nullable = false)
	private Integer availableCopies;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.AVAILABLE;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonIgnoreProperties("book")
	private List<BookBarcode> barcodes = new ArrayList<>();

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (totalCopies == null)
			totalCopies = 1;
		if (availableCopies == null)
			availableCopies = totalCopies;
		if (status == null)
			status = Status.AVAILABLE;
		if (isDeleted == null)
			isDeleted = false;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getAuthor() { return author; }
	public void setAuthor(String author) { this.author = author; }
	public String getPublisher() { return publisher; }
	public void setPublisher(String publisher) { this.publisher = publisher; }
	public BookCategory getCategory() { return category; }
	public void setCategory(BookCategory category) { this.category = category; }
	public String getEdition() { return edition; }
	public void setEdition(String edition) { this.edition = edition; }
	public String getYear() { return year; }
	public void setYear(String year) { this.year = year; }
	public String getLanguage() { return language; }
	public void setLanguage(String language) { this.language = language; }
	public String getAccessUrl() { return accessUrl; }
	public void setAccessUrl(String accessUrl) { this.accessUrl = accessUrl; }
	public String getFormat() { return format; }
	public void setFormat(String format) { this.format = format; }
	public String getDigitalType() { return digitalType; }
	public void setDigitalType(String digitalType) { this.digitalType = digitalType; }
	@JsonProperty("licenseExpiry")
	public String getLicenseExpiry() { return licenseExpiry; }
	@JsonProperty("licenseExpiry")
	public void setLicenseExpiry(String licenseExpiry) { this.licenseExpiry = licenseExpiry; }
	@JsonProperty("usageLimit")
	public Integer getUsageLimit() { return usageLimit; }
	@JsonProperty("usageLimit")
	public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
	public BookType getType() { return type; }
	public void setType(BookType type) { this.type = type; }
	public String getIsbn() { return isbn; }
	public void setIsbn(String isbn) { this.isbn = isbn; }
	public String getShelfLocation() { return shelfLocation; }
	public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }
	public Integer getTotalCopies() { return totalCopies; }
	public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
	public Integer getAvailableCopies() { return availableCopies; }
	public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
	public Boolean getIsDeleted() { return isDeleted; }
	public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
	public List<BookBarcode> getBarcodes() { return barcodes; }
	public void setBarcodes(List<BookBarcode> barcodes) { this.barcodes = barcodes; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public String getCreatedBy() { return createdBy; }
	public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

	public enum BookType {
		PHYSICAL,
		DIGITAL
	}

	public enum Status {
		AVAILABLE,
		UNAVAILABLE,
		LOST,
		RESERVED
	}
}
