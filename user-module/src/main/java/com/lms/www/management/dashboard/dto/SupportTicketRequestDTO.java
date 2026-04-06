package com.lms.www.management.dashboard.dto;

import lombok.Data;

@Data
public class SupportTicketRequestDTO {
    private String subject;
    private String description;
    private com.lms.www.management.enums.TicketCategory category;
    private com.lms.www.management.enums.TicketPriority priority;
}