package com.lms.www.management.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.www.management.enums.WebinarPollStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "webinar_polls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebinarPoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_id")
    private Long pollId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webinar_id", nullable = false)
    @JsonIgnore
    private Webinar webinar;

    @Column(name = "question", nullable = false, length = 500)
    private String question;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "webinar_poll_options", joinColumns = @JoinColumn(name = "poll_id"))
    @Column(name = "option_text")
    @Builder.Default
    private List<String> options = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private WebinarPollStatus status = WebinarPollStatus.DRAFT;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = WebinarPollStatus.DRAFT;
        }
    }
    @OneToMany(mappedBy = "poll", cascade = CascadeType.REMOVE)
    private List<WebinarPollResponse> responses;
}