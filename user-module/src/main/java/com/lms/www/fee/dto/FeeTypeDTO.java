package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeTypeDTO {
    private Long id;
    private String name;
    private boolean active;
}