package com.lms.www.marketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class LandingPageRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Slug is required")
    private String slug;
    
    private String headline;
    private String subtitle;
    
    @NotNull(message = "Price is required")
    private BigDecimal price;
    
    private BigDecimal adBudget;
    private String videoUrl;
    private List<String> features;
    private String ctaText;
}
