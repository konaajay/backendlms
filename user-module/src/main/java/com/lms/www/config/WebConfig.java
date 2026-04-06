package com.lms.www.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 🔥 Serve all uploaded files
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/") // relative to project root
                .setCachePeriod(3600); // optional: 1 hour caching
    }
}