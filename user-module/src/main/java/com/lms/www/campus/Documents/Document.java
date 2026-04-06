package com.lms.www.campus.Documents;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "documents")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private DocumentCategory category;

    private String title;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;

    private Long uploadedBy;
    private Long ownerUserId;

    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel = AccessLevel.PRIVATE;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private Boolean isDeleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum AccessLevel {
        ADMIN, STAFF, STUDENT, PRIVATE
    }

    public enum Status {
        ACTIVE, ARCHIVED
    }
}
