package com.lms.www.fee.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private Boolean sent;
    private LocalDateTime createdAt;
}
