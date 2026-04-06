package com.lms.www.campus.Documents;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "document_versions", uniqueConstraints = @UniqueConstraint(columnNames = { "document_id",
        "version_number" }))
@Data
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long versionId;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    private Integer versionNumber;
    private String fileName;
    private String filePath;
    private Long fileSize;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
