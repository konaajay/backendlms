package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "webinar_poll_responses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebinarPollResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private WebinarPoll poll;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "participant_id")
    private Long participantId;

    @Column(name = "selected_option", nullable = false)
    private String selectedOption;

    @Column(name = "responded_at", updatable = false)
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        this.respondedAt = LocalDateTime.now();
    }
}