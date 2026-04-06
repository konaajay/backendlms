package com.lms.www.affiliate.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadNoteDTO {
    private Long id;
    private Long leadId;
    private String note;
    private String createdBy;
    private LocalDateTime createdAt;
}
